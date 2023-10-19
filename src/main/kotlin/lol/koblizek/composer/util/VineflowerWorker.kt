package lol.koblizek.composer.util

import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.RuntimeConfiguration
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import java.io.File

abstract class VineflowerWorker : WorkAction<VineflowerWorker.Parameters> {
    interface Parameters : WorkParameters {
        var files: Set<File>
        var config: RuntimeConfiguration
        var sourceFile: File
    }

    override fun execute() {
        // val project = ComposerPlugin.project }

        val props: MutableMap<String, Any> = HashMap(IFernflowerPreferences.DEFAULTS)
        props[IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES] = "1"
        props[IFernflowerPreferences.REMOVE_SYNTHETIC] = "1"
        props[IFernflowerPreferences.INCLUDE_ENTIRE_CLASSPATH] = "1"
        props[IFernflowerPreferences.PATTERN_MATCHING] = "1"
        props[IFernflowerPreferences.TERNARY_CONDITIONS] = "1"
        props[IFernflowerPreferences.THREADS] = (Runtime.getRuntime().availableProcessors() / 2).toString()

        val dir = File(parameters.config.decompilationSource!!)

        val fernFlower = Fernflower(
            DirectoryResultSaver(dir),
            props,
            Logger()
        )
        fernFlower.addSource(parameters.sourceFile)
        parameters.files.forEach {
            fernFlower.addLibrary(it)
        }
        fernFlower.decompileContext()
        parameters.config.toRemove.forEach {
            val file = dir.toPath().resolve(it).toFile()
            if (file.exists()) {
                if (file.isDirectory) file.deleteRecursively()
                else file.delete()
            }
        }
        if (parameters.config.resourcesSource != null) {
            parameters.config.resources.forEach {
                FileUtils.moveToDirectory(File(it), File(parameters.config.resourcesSource!!), true)
            }
        }
        parameters.config.moveToRoot.forEach {
            val root = parameters.config.decompilationSource
            val file = File(root!!).resolve(it)
            FileUtils.moveToDirectory(file, File(root), true)
        }
    }

    class Logger : IFernflowerLogger() {
        override fun writeMessage(message: String, severity: Severity) {
            if (severity.ordinal >= Severity.WARN.ordinal) {
                println(message)
            }
        }

        override fun writeMessage(message: String, severity: Severity, t: Throwable) {
            if (severity.ordinal >= Severity.WARN.ordinal) {
                println(message)
            }
        }

    }
}