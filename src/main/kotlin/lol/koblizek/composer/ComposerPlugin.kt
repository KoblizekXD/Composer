package lol.koblizek.composer

import lol.koblizek.composer.actions.*
import lol.koblizek.composer.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

class ComposerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        project = target
        target.tasks.create("cleanUp", CleanUpTask::class.java)
        target.tasks.create("genPatches", GenPatchesTask::class.java)
        target.tasks.create("applyPatches", ApplyPatchesTask::class.java)
        genFiles = target.tasks.create("genFiles", GenFilesTask::class.java)
        deobfGame = target.tasks.create("deobfGame", DeobfuscateTask::class.java)
        downloadMappings = target.tasks.create("downloadMappings", DownloadMappingsTask::class.java)
        decompileGame = target.tasks.create("decompileGame", DecompileTask::class.java)
        target.tasks.getByName("build") {
            it.setDependsOn(arrayListOf(genFiles, downloadMappings, deobfGame, decompileGame))
        }
    }

    companion object {
        lateinit var project: Project
        lateinit var version: String
        lateinit var config: RuntimeConfiguration
        lateinit var genFiles: GenFilesTask
        lateinit var deobfGame: DeobfuscateTask
        lateinit var downloadMappings: DownloadMappingsTask
        lateinit var decompileGame: DecompileTask

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

fun composerRepositories(repositoryHandler: RepositoryHandler) {
    repositoryHandler.maven {
        it.url = URI("https://maven.neoforged.net/releases")
    }
    repositoryHandler.maven {
        it.url = URI("https://maven.fabricmc.net/")
    }
    repositoryHandler.maven {
        it.url = URI("https://jitpack.io")
    }
}