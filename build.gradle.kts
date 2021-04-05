import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("nebula.info-scm") version "9.3.0"
    kotlin("jvm") version "1.4.31"
    jacoco
    application
//    kotlin("kapt") version "1.4.32"
}

group = "me.jlengrand"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "1.5.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")

    implementation("info.picocli:picocli:4.6.1")
//    kapt("info.picocli:picocli-codegen:4.6.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.2")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

//kapt {
//    arguments {
//        arg("project", "${project.group}/${project.name}")
//    }
//}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "me.lengrand.mygoodfirstissues.CliFirstGoodIssues"
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = "me.lengrand.mygoodfirstissues.CliFirstGoodIssues"
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}