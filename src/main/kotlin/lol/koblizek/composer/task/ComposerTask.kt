package lol.koblizek.composer.task

import org.gradle.api.DefaultTask
import java.io.File
import java.net.URL

abstract class ComposerTask : DefaultTask() {

    fun download(name: String, url: String): File {
        val uri = URL(url)
        val path = temporaryDir.resolve(name)
        uri.openStream().use { input ->
            path.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return path
    }
}