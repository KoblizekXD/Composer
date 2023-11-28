package lol.koblizek.composer.task

import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.task.worker.VineflowerWorker
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class DecompileTask : DefaultTask() {

    init {
        group = "composer"
        description = "Decompiles source code"
        dependsOn("deobfGame")
    }

    @Inject
    public abstract fun getWorkerExecutor(): WorkerExecutor

    @TaskAction
    fun run() {
        if (ComposerPlugin.config.decompileOptions.isTargetDirInitialized()) {
            val queue = getWorkerExecutor().processIsolation {
                it.forkOptions { java ->
                    java.maxHeapSize = "2G"
                }
            }
            queue.submit(VineflowerWorker::class.java) {
                it.libraries = (project.configurations.getByName("compileClasspath").files)
                it.config = ComposerPlugin.config.decompileOptions
                it.sourceFile = ComposerPlugin.deobfGame.temporaryDir.resolve("minecraft-deobf.jar")
            }
        } else {
            println("Skipping decompilation...")
        }
    }
}