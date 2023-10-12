package lol.koblizek.composer

import lol.koblizek.composer.tasks.DownloadMappingsTask
import lol.koblizek.composer.tasks.GenFilesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        genFilesTask = target.tasks.create("genFiles", GenFilesTask::class.java)
        val downloadMappings = target.tasks.create("downloadMappings", DownloadMappingsTask::class.java)
        target.task("dependencies").dependsOn(genFilesTask, downloadMappings)
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
        lateinit var config: RuntimeConfiguration
        lateinit var genFilesTask: GenFilesTask
    }
}

// This belongs to dependencies {} & add dependencies
fun minecraft(mc: String) {
    ComposerPlugin.version = mc
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
    val config = RuntimeConfiguration()
    cfg(config)
    ComposerPlugin.config = config
}