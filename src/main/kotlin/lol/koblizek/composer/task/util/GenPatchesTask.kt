package lol.koblizek.composer.task.util

import codechicken.diffpatch.DiffOperation
import lol.koblizek.composer.ComposerPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path

abstract class GenPatchesTask : DefaultTask() {
    init {
        group = "composer"
        description = "Generate patches that are redistributable"
    }

    @TaskAction
    fun generate() {
        val dir = project.file("patches/")
        if (!dir.exists()) dir.mkdirs()
        DiffOperation.builder()
            .aPath(ComposerPlugin.decompileGame.temporaryDir.resolve("origin/").toPath())
            .bPath(Path.of(ComposerPlugin.config.decompilationSource))
            .aPrefix(null)
            .bPrefix(null)
            .filter { it.endsWith(".java") }
            .outputPath(dir.toPath())
            .build().doDiff()

    }
}