package lol.koblizek.composer.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class SetupWorkspaceTask : DefaultTask() {
    init {
        group = "composer"
        description = "Setups a workspace according to provided sources and options"
        dependsOn("genFiles")
    }

    @TaskAction
    fun execute() {
        println("\nDone! The project setup was successful")
        println("If you encountered any problems feel free to get support on our github!\n")
    }
}