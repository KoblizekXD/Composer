package lol.koblizek.composer

import lol.koblizek.composer.actions.*
import lol.koblizek.composer.task.ApplyPatchesTask
import lol.koblizek.composer.task.CleanUpTask
import lol.koblizek.composer.task.GenPatchesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("cleanUp", CleanUpTask::class.java)
        target.tasks.create("genPatches", GenPatchesTask::class.java)
        target.tasks.create("applyPatches", ApplyPatchesTask::class.java)
        project = target
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
        lateinit var config: RuntimeConfiguration

        fun isConfigInitialized(): Boolean = ::config.isInitialized
    }
}

// This belongs to dependencies {} & add dependencies
fun minecraft(mc: String) {
    ComposerPlugin.version = mc
    GenFilesAction().start(ComposerPlugin.project)
    DownloadMappingsAction().start(ComposerPlugin.project)
    DeobfuscateAction().start(ComposerPlugin.project)
    LoadLibrariesAction().start(ComposerPlugin.project)
    DecompileAction().start(ComposerPlugin.project)
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
    val config = RuntimeConfiguration()
    cfg(config)
    ComposerPlugin.config = config
}