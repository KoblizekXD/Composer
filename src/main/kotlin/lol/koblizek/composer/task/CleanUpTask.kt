package lol.koblizek.composer.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

abstract class CleanUpTask : DefaultTask() {
    init {
        description = "Cleans up all Composer files"
        group = "composer"
    }

    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun cleanup() {
        project.projectDir.toPath().resolve("composer").deleteRecursively()
    }
}