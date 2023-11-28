package lol.koblizek.composer.task

import lol.koblizek.composer.ComposerPlugin
import lol.koblizek.composer.task.worker.DeobfuscationWorker
import lol.koblizek.composer.task.worker.VineflowerWorker
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.MappingWriter
import net.fabricmc.mappingio.adapter.MappingNsCompleter
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch
import net.fabricmc.mappingio.format.MappingFormat
import net.fabricmc.tinyremapper.NonClassCopyMode
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import net.fabricmc.tinyremapper.TinyUtils
import net.minecraftforge.fart.api.Renamer
import net.minecraftforge.fart.api.SignatureStripperConfig
import net.minecraftforge.fart.api.SourceFixerConfig
import net.minecraftforge.fart.api.Transformer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.*
import java.util.regex.Pattern
import javax.inject.Inject

abstract class DeobfuscateTask : DefaultTask() {

    init {
        group = "composer"
        description = "Used to deobfuscate initial source code into human-readable code, also applies ART if requested"
        dependsOn("genFiles")
    }

    @Inject
    public abstract fun getWorkerExecutor(): WorkerExecutor

    @TaskAction
    fun run() {
        var unDeobf = ComposerPlugin.genFiles.temporaryDir.resolve("minecraft.jar")
        val mappings = if (ComposerPlugin.genFiles.newMappings == null)
            ComposerPlugin.genFiles.temporaryDir.resolve("mappings")
        else ComposerPlugin.genFiles.newMappings
        if (ComposerPlugin.config.remapOptions.applyArt) {
            Renamer.builder().add(Transformer.recordFixerFactory())
                .add(Transformer.sourceFixerFactory(SourceFixerConfig.JAVA))
                .add(Transformer.signatureStripperFactory(SignatureStripperConfig.ALL)).build()
                .run(unDeobf, temporaryDir.resolve("minecraft-art.jar"))
            unDeobf = temporaryDir.resolve("minecraft-art.jar")
        }
        val deobf = temporaryDir.resolve("minecraft-deobf.jar")

        val queue = getWorkerExecutor().processIsolation {
            it.forkOptions { java ->
                java.maxHeapSize = "2G"
            }
        }
        queue.submit(DeobfuscationWorker::class.java) {
            it.inputJar = unDeobf
            it.mappings = mappings!!
            it.outputPath = deobf
            it.options = ComposerPlugin.config.remapOptions
        }
    }
}