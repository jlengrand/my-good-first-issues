import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("nebula.info-scm") version "9.3.0"
    kotlin("jvm") version "1.4.31"
    application
}

group = "me.jlengrand"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.2")
    implementation("org.kohsuke:github-api:1.123")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}