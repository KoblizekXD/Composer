package lol.koblizek.composer.actions

import org.gradle.api.Project
import java.io.File
import kotlin.io.path.createDirectories

abstract class Action() {
    internal lateinit var temporaryDir: File

    abstract fun run(project: Project)

    fun start(project: Project) {
        this::class.simpleName?.let {
            temporaryDir = project.projectDir.toPath().resolve("composer")
                .resolve("actions").resolve(it).createDirectories().toFile()
        }
        run(project)
    }
}