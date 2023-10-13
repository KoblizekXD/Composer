plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "lol.koblizek"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "neoforgedReleases"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.MCPHackers:DiffPatch:cde1224")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("commons-io:commons-io:2.14.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.neoforged:AutoRenamingTool:1.0.7")
    implementation("org.vineflower:vineflower:1.9.3")
    implementation("net.fabricmc:tiny-remapper:0.8.7")
    implementation("net.fabricmc:mapping-io:0.4.2")
    implementation(gradleApi())
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    website = "https://github.com/ComposeMC/Composer"
    vcsUrl = "https://github.com/ComposeMC/Composer"

    plugins {
        create("composer") {
            id = "lol.koblizek.composer"
            displayName = "Composer"
            description = "Plugin which makes creating Minecraft server development much easier!"
            implementationClass = "lol.koblizek.composer.ComposerPlugin"
            tags = listOf("minecraft", "decompilation", "deobfuscation", "plugins")
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "lol.koblizek"
            artifactId = "composer"
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}