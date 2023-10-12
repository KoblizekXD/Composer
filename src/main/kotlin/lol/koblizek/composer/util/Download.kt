package lol.koblizek.composer.util

import org.gradle.api.Task
import java.io.File
import java.net.URL

class Download(dir: File, url: String, name: String) {
    internal val file: File

    init {
        val uri = URL(url)
        val path = dir.toPath().resolve(name)
        file = path.toFile()
        uri.openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    constructor(task: Task, url: String, name: String) : this(task.temporaryDir, url, name)
}
