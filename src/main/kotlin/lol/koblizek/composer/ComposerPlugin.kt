package lol.koblizek.composer

import lol.koblizek.composer.actions.LoadLibrariesAction
import lol.koblizek.composer.actions.DeobfuscateAction
import lol.koblizek.composer.actions.DownloadMappingsAction
import lol.koblizek.composer.actions.GenFilesAction
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
    GenFilesAction().start(ComposerPlugin.project)
    DownloadMappingsAction().start(ComposerPlugin.project)
    DeobfuscateAction().start(ComposerPlugin.project)
    LoadLibrariesAction().start(ComposerPlugin.project)
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
    val config = RuntimeConfiguration()
    cfg(config)
    ComposerPlugin.config = config
}