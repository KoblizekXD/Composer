package lol.koblizek.composer.actions

import lol.koblizek.composer.util.VineflowerWorker
import org.gradle.api.Project
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class DecompileAction : Action() {
    @Inject
    public abstract fun getWorkerExecutor(): WorkerExecutor;

    override fun run(project: Project) {
        val queue = getWorkerExecutor().processIsolation {
            it.forkOptions { java ->
                java.maxHeapSize = "2G"
            }
        }
        queue.submit(VineflowerWorker::class.java) {
            it.project = project
        }
    }
}