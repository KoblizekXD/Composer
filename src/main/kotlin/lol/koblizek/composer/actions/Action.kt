package lol.koblizek.composer.actions

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.createFile
import kotlin.io.path.exists

abstract class Action() : DefaultTask() {
    @TaskAction
    abstract fun run(project: Project)

    fun start(project: Project) {
        if (this::class.simpleName != "LoadLibrariesAction" && temporaryDir.toPath().resolve("done.txt").exists()) return
        run(project)
        temporaryDir.toPath().resolve("done.txt").createFile()
    }
}