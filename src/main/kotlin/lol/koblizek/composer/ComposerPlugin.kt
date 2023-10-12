package lol.koblizek.composer

import lol.koblizek.composer.actions.LoadLibrariesAction
import lol.koblizek.composer.tasks.DeobfuscateTask
import lol.koblizek.composer.tasks.DownloadMappingsTask
import lol.koblizek.composer.tasks.GenFilesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        project = target
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
        lateinit var config: RuntimeConfiguration
    }
}

// This belongs to dependencies {} & add dependencies
fun minecraft(mc: String) {
    ComposerPlugin.version = mc
    GenFilesTask().start(ComposerPlugin.project)
    DownloadMappingsTask().start(ComposerPlugin.project)
    DeobfuscateTask().start(ComposerPlugin.project)
    LoadLibrariesAction().start(ComposerPlugin.project)
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
    val config = RuntimeConfiguration()
    cfg(config)
    ComposerPlugin.config = config
}