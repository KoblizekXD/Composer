package lol.koblizek.composer.task

import lol.koblizek.composer.util.VineflowerWorker
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class DecompileTask @Inject constructor(val workerExecutor: WorkerExecutor) : DefaultTask() {
    @TaskAction
    fun run() {
        val queue = workerExecutor.processIsolation {
            it.forkOptions { java ->
                java.maxHeapSize = "2G"
            }
        }
        queue.submit(VineflowerWorker::class.java) {
            it.project = project
        }
    }
}