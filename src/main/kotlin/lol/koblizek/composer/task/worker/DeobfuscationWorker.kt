package lol.koblizek.composer.task.worker

import lol.koblizek.composer.RuntimeConfiguration
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.MappingWriter
import net.fabricmc.mappingio.adapter.MappingNsCompleter
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch
import net.fabricmc.mappingio.format.MappingFormat
import net.fabricmc.tinyremapper.NonClassCopyMode
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import net.fabricmc.tinyremapper.TinyUtils
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.*
import java.util.regex.Pattern

abstract class DeobfuscationWorker : WorkAction<DeobfuscationWorker.DeobfuscationWorkerParameters> {
    interface DeobfuscationWorkerParameters : WorkParameters {
        var inputJar: File
        var outputPath: File
        var mappings: File
        var options: RuntimeConfiguration.RemapOptions
    }

    override fun execute() {
        val writer = StringWriter()
        MappingWriter.create(writer, MappingFormat.TINY_2_FILE).use { mapper ->
            MappingReader.read(
                parameters.mappings.toPath(), MappingNsCompleter(
                    MappingSourceNsSwitch(mapper, "official", true), emptyMap<String, String>()
                )
            )
        }
        val remapper = TinyRemapper.newRemapper().invalidLvNamePattern(Pattern.compile("\\$\\$\\d+"))
            .renameInvalidLocals(true)
            .inferNameFromSameLvIndex(true)
            .withMappings(
                TinyUtils.createTinyMappingProvider(
                    BufferedReader(StringReader(writer.toString())),
                    "official",
                    parameters.options.nms
                )
            ).build()
        writer.close()
        try {
            OutputConsumerPath.Builder(parameters.outputPath.toPath()).build().use { outputConsumer ->
                outputConsumer.addNonClassFiles(parameters.inputJar.toPath(), NonClassCopyMode.FIX_META_INF, remapper)
                remapper.readInputs(parameters.inputJar.toPath())
                remapper.apply(outputConsumer)
                outputConsumer.close()
            }
        } catch (e: IOException) {
            println("An error occurred during deobfuscation:")
            e.printStackTrace()
        } finally {
            remapper.finish()
        }
    }
}