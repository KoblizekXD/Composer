plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

group = "lol.koblizek"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "neoforgedReleases"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
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