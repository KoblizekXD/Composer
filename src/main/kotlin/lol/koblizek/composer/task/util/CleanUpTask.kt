package lol.koblizek.composer.task.util

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class CleanUpTask : DefaultTask() {
    init {
        description = "Cleans up all Composer files"
        group = "composer"
    }

    @TaskAction
    fun cleanup() {
        project.tasks.forEach {  task ->
            if (task.group == "composer") {
                val result = task.temporaryDir.deleteRecursively()
                if (!result) {
                    println("Failed to remove temporary directory for task ${task.name}.")
                }
            }
        }
    }
}