import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.60"
}

group = "org.apache.geode"
version = "1.8.0-SNAPSHOT"

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "io.micrometer", name = "micrometer-core", version = "1.0.5")
    compile(group = "io.micrometer", name = "micrometer-registry-influx", version = "1.0.5")
    compile(group = "io.micrometer", name = "micrometer-registry-jmx", version = "1.0.5")
    compile(group = "io.micrometer", name = "micrometer-registry-prometheus", version = "1.0.5")
    compileOnly( project(":geode-common"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.suppressWarnings = true
}