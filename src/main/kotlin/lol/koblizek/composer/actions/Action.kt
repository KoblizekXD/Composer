package lol.koblizek.composer.actions

import org.gradle.api.Project

abstract class Action() {
    abstract fun run(project: Project)
}