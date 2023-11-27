package lol.koblizek.composer.task

import org.gradle.api.DefaultTask

abstract class SetupWorkspaceTask : DefaultTask() {
    init {
        group = "composer"
        description = "Setups a workspace according to provided sources and options"
    }
}