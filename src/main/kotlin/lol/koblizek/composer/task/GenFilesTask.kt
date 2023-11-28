package lol.koblizek.composer.task

import com.google.gson.Gson
import com.google.gson.JsonObject
import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.RuntimeConfiguration
import lol.koblizek.composer.util.Download
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter
import java.net.URL
import java.nio.file.Files
import java.util.zip.ZipFile

abstract class GenFilesTask : ComposerTask() {

    init {
        group = "composer"
        description = "Generates file required for workspace setup"
    }

    @TaskAction
    fun run() {
        val config = ComposerPlugin.config

        if (config.areDataSourcesInitialized()) {
            val src = config.dataSources
            if (src.isEverythingInitialized()) {
                download("minecraft-original.jar", src.game)
                val file = download("minecraft-data.json", src.gameJson)
                val maps = download("mappings", src.mappings)

                if (src.doUseAltMappingFile()) {
                    src.newMappings(maps)
                }

                if (file.exists()) {
                    val json = Gson().fromJson(file.readText(), JsonObject::class.java)
                    val libraries = json.getAsJsonArray("libraries")
                    Gson().toJson(libraries, FileWriter(temporaryDir.resolve("libraries.json")))
                } else {
                    logger.warn("Warning! Couldn't prepare the library json because minecraft-data.json is missing!")
                }
            } else
                logger.error("Error: Failed to generate required files: Missing property/properties")
        } else {
            logger.error("Error: Failed to generate required files: Missing dataSources block")
        }
    }
}