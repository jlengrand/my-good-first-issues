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
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    val mavenVersion = "3.8.1"
    val mavenResolverVersion = "1.6.2"
    implementation("org.apache.maven:maven-model:$mavenVersion")
    implementation("org.apache.maven:maven-plugin-api:$mavenVersion")
    implementation("org.apache.maven:maven-core:$mavenVersion")
    implementation("org.apache.maven:maven-resolver-provider:$mavenVersion")
    implementation("org.eclipse.aether:aether-connector-basic:1.1.0")
    implementation("org.eclipse.aether:aether-transport-file:1.1.0")
    implementation("org.eclipse.aether:aether-transport-http:1.1.0")
    implementation("org.eclipse.aether:aether-transport-wagon:1.1.0")

    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")

    val ktorVersion = "1.5.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")

    val mockkVersion = "1.10.6"
    testImplementation("io.mockk:mockk:$mockkVersion")

    implementation("info.picocli:picocli:4.6.1")
//    kapt("info.picocli:picocli-codegen:4.6.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.2")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation(kotlin("stdlib-jdk8"))
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
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}