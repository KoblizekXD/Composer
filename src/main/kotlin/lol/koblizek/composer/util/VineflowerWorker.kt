package lol.koblizek.composer.util

import lol.koblizek.composer.RuntimeConfiguration
import org.apache.commons.io.FileUtils
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import java.io.File

abstract class VineflowerWorker : WorkAction<VineflowerWorker.Parameters> {
    interface Parameters : WorkParameters {
        var libraries: Set<File>
        var config: RuntimeConfiguration
        var sourceFile: File
    }

    override fun execute() {
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
        parameters.libraries.forEach {
            fernFlower.addLibrary(it)
        }
        fernFlower.decompileContext()
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