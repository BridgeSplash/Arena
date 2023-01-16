import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "net.minestom"
version = "1.0.1"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    flatDir {
        dirs("$rootDir/libs")
    }
}

dependencies {
    compileOnly("com.github.Minestom:Minestom:8eb089bf3e")
    compileOnly("mysql:mysql-connector-java:8.0.30")
    compileOnly("de.simonsator.partyandfriends:PAF-API:1.5.4")
    compileOnly("de.simonsator.partyandfriends:PAF-Party-API:1.0.4")

    implementation("de.articdive:jnoise-pipeline:4.0.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
    }

    build { dependsOn(shadowJar) }
}
