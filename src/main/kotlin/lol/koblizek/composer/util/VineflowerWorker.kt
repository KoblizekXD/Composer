package lol.koblizek.composer.util

import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.task.DeobfuscateAction
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import java.io.File

abstract class VineflowerWorker : WorkAction<VineflowerWorker.Parameters> {
    interface Parameters : WorkParameters {
        var project: Project
    }

    override fun execute() {
        if (!ComposerPlugin.isConfigInitialized() || ComposerPlugin.config.decompilationSource == null) {
            println("Cannot decompile: Missing runtimeConfig block or decompilationSource parameter")
            println("Run cleanUp task and rebuild the project once you fix the problem")
            return
        }

        val props: MutableMap<String, Any> = HashMap(IFernflowerPreferences.DEFAULTS)
        props[IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES] = "1"
        props[IFernflowerPreferences.REMOVE_SYNTHETIC] = "1"
        props[IFernflowerPreferences.INCLUDE_ENTIRE_CLASSPATH] = "1"
        props[IFernflowerPreferences.PATTERN_MATCHING] = "1"
        props[IFernflowerPreferences.TERNARY_CONDITIONS] = "1"
        props[IFernflowerPreferences.THREADS] = (Runtime.getRuntime().availableProcessors() / 2).toString()

        val dir = File(ComposerPlugin.config.decompilationSource!!)

        val fernFlower = Fernflower(
            DirectoryResultSaver(dir),
            props,
            Logger()
        )
        fernFlower.addSource(DeobfuscateAction().temporaryDir.resolve("server-deobf.jar"))
        this.parameters.project.configurations.getByName("compileClasspath").files.forEach {
            fernFlower.addLibrary(it)
        }
        fernFlower.decompileContext()
        ComposerPlugin.config.toRemove.forEach {
            val file = dir.toPath().resolve(it).toFile()
            if (file.exists()) {
                if (file.isDirectory) file.deleteRecursively()
                else file.delete()
            }
        }
        if (ComposerPlugin.config.resourcesSource != null) {
            ComposerPlugin.config.resources.forEach {
                FileUtils.moveToDirectory(File(it), File(ComposerPlugin.config.resourcesSource!!), true)
            }
        }
        ComposerPlugin.config.moveToRoot.forEach {
            val root = ComposerPlugin.config.decompilationSource
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