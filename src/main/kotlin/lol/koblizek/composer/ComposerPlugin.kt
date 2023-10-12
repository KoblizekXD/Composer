package lol.koblizek.composer

import lol.koblizek.composer.tasks.DownloadMappingsTask
import lol.koblizek.composer.tasks.GenFilesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("genFiles", GenFilesTask::class.java)
        target.tasks.create("downloadMappings", DownloadMappingsTask::class.java)
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
    }
}

// This belongs to dependencies {}
fun minecraft(mc: String) {
    ComposerPlugin.version = mc
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
}