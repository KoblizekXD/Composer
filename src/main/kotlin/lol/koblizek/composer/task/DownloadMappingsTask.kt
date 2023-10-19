package lol.koblizek.composer.task

import com.google.gson.Gson
import com.google.gson.JsonArray
import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.util.Download
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.InputStreamReader
import java.net.URL
import java.util.zip.ZipFile

abstract class DownloadMappingsTask : DefaultTask() {

    init {
        group = "composer"
        description = "Downloads mapping files"
    }

    private fun getMappingsUrl(gameVersion: String): String {
        val array = readJsonArray(gameVersion)
        val latestMappings = array[0].asJsonObject.getAsJsonPrimitive("version").asString
        return "https://maven.fabricmc.net/net/fabricmc/yarn/$latestMappings/yarn-$latestMappings-mergedv2.jar"
    }

    private fun readJsonArray(version: String): JsonArray {
        return Gson().fromJson(
            InputStreamReader(URL("https://meta.fabricmc.net/v2/versions/yarn/$version").openStream()),
            JsonArray::class.java)
    }

    @TaskAction
    fun run() {
        val mappings = getMappingsUrl(ComposerPlugin.version)
        val zip = ZipFile(Download(temporaryDir, mappings, "mappings.zip").file)
        val tiny = temporaryDir.toPath().resolve("mappings.tiny").toFile()
        FileUtils.copyInputStreamToFile(
            zip.getInputStream(zip.getEntry("mappings/mappings.tiny")),
            tiny
        )
    }
}