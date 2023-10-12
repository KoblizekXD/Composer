package lol.koblizek.composer.actions

import com.google.gson.Gson
import com.google.gson.JsonObject
import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.util.Download
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.nio.file.Files
import java.util.zip.ZipFile

class GenFilesAction : Action() {

    override fun run(project: Project) {
        val manifest = Download(temporaryDir, "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json", "version_manifest.json").file
        val json = Gson().fromJson(manifest.readText(), JsonObject::class.java)
        val obj = json.getAsJsonArray("versions")
            .find {
                it.asJsonObject.getAsJsonPrimitive("id")
                    .asString == ComposerPlugin.version
            } ?: return
        val url = obj.asJsonObject.getAsJsonPrimitive("url").asString
        val versionData = Gson().fromJson(Download(temporaryDir, url, "version_data.json").file.readText(), JsonObject::class.java)
        val server = Download(temporaryDir, versionData.getAsJsonObject("downloads")
            .getAsJsonObject("server").getAsJsonPrimitive("url").asString, "server.jar").file
        if (ComposerPlugin.isConfigInitialized() && ComposerPlugin.config.useInstead != null) {
            val zip = ZipFile(server)
            val temp = temporaryDir.toPath().resolve("server-temp.jar").toFile()
            val ins = zip.getInputStream(zip.getEntry(ComposerPlugin.config.useInstead!!))
            FileUtils.copyInputStreamToFile(
                ins,
                temp
            )
            ins.close()
            zip.close()
            Files.delete(server.toPath())
            temp.renameTo(server)
        }
        temporaryDir.toPath().resolve("libraries.json").toFile().writer().use {
            Gson().toJson(versionData.getAsJsonArray("libraries"), it)
        }
    }
}