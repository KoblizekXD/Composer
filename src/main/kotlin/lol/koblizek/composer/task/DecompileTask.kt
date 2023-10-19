package lol.koblizek.composer.task

import lol.koblizek.composer.util.VineflowerWorker
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class DecompileTask : DefaultTask() {

    init {
        group = "composer"
        description = "Decompiles source code"
    }

    @Inject
    public abstract fun getWorkerExecutor(): WorkerExecutor

    @TaskAction
    fun run() {
        if (temporaryDir.resolve("checked").exists()) return
        val queue = getWorkerExecutor().processIsolation {
            it.forkOptions { java ->
                java.maxHeapSize = "2G"
            }
        }
        queue.submit(VineflowerWorker::class.java) {}
        temporaryDir.resolve("checked").createNewFile()
    }
}