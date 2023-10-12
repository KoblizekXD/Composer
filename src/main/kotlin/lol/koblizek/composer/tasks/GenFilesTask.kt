package lol.koblizek.composer.tasks

import com.google.gson.Gson
import com.google.gson.JsonObject
import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.actions.Action
import lol.koblizek.composer.util.Download
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class GenFilesTask : Action() {

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
        Download(temporaryDir, versionData.getAsJsonObject("downloads")
            .getAsJsonObject("server").getAsJsonPrimitive("url").asString, "server.jar")
        temporaryDir.toPath().resolve("libraries.json").toFile().writer().use {
            Gson().toJson(versionData.getAsJsonArray("libraries"), it)
        }
    }
}