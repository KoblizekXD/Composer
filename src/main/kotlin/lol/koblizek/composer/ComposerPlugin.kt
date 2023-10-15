package lol.koblizek.composer

import lol.koblizek.composer.actions.*
import lol.koblizek.composer.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("cleanUp", CleanUpTask::class.java)
        target.tasks.create("genPatches", GenPatchesTask::class.java)
        target.tasks.create("applyPatches", ApplyPatchesTask::class.java)
        genFiles = target.tasks.create("genFiles", GenFilesAction::class.java)
        deobfGame = target.tasks.create("deobfGame", DeobfuscateAction::class.java)
        downloadMappings = target.tasks.create("downloadMappings", DownloadMappingsAction::class.java)
        decompileGame = target.tasks.create("decompileGame", DecompileAction::class.java)
        target.tasks.getByName("dependencies") {
            it.setDependsOn(arrayListOf(genFiles, downloadMappings, decompileGame))
        }

        project = target
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
        lateinit var config: RuntimeConfiguration
        lateinit var genFiles: GenFilesAction
        lateinit var deobfGame: DeobfuscateAction
        lateinit var downloadMappings: DownloadMappingsAction
        lateinit var decompileGame: DecompileAction

        fun isConfigInitialized(): Boolean = ::config.isInitialized
    }
}

// Routes

// If runtimeConfig block is present run genFiles, downloadMappings deobfuscateGame and decompileGame tasks
// if there's minecraftLibraries() in  dependencies load libraries

// This belongs to dependencies {} & add dependencies
fun minecraftLibraries() {
    LoadLibrariesAction().start(ComposerPlugin.project)
}

fun runtimeConfig(cfg: RuntimeConfiguration.() -> Unit) {
    val config = RuntimeConfiguration()
    cfg(config)
    ComposerPlugin.config = config
}