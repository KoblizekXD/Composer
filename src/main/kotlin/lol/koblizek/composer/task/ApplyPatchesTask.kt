package lol.koblizek.composer.task

import codechicken.diffpatch.PatchOperation
import codechicken.diffpatch.util.PatchMode
import lol.koblizek.composer.ComposerPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ApplyPatchesTask : DefaultTask() {
    init {
        group = "composer"
        description = "Applies patches to source code"
    }

    @TaskAction
    fun apply() {
        val patchDir = project.file("patches/")
        val source = File(ComposerPlugin.config.decompilationSource)
        val patchOperation = PatchOperation.builder()
            .basePath(source.toPath())
            .patchesPath(patchDir.toPath())
            .outputPath(source.toPath())
            .mode(PatchMode.OFFSET)
            .build()
        val status = patchOperation.doPatch()
    }
}