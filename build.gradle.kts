plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
    `maven-publish`
}

group = "io.github.orioncraftmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks {
    build.get().dependsOn(shadowJar)
    shadowJar {
        archiveClassifier.set(null as String?)
        // Keep these as is
        arrayOf("org.lwjgl.nanovg", "org.lwjgl.util.yoga").forEach {
            relocate(it, it)
        }

        // Relocate these to a different package
        arrayOf("org.lwjgl").forEach {
            relocate(it, "${project.group}.libs.$it")
        }
    }
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:3.2.3"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-nanovg")
    implementation("org.lwjgl", "lwjgl-yoga")

    listOf("windows", "linux", "macos").forEach { osName ->
        val nativeClassifier = "natives-$osName"

        runtimeOnly("org.lwjgl", "lwjgl", classifier = nativeClassifier)
        runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = nativeClassifier)
        runtimeOnly("org.lwjgl", "lwjgl-yoga", classifier = nativeClassifier)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"])
        }
    }
}
