package me.lengrand.mygoodfirstissues.parsers.maven

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class MavenClientTest {
    private val minimalResourceStreamPom = javaClass.classLoader.getResourceAsStream("poms/minimal-pom.xml")
    private val minimalResourcePom = javaClass.classLoader.getResource("poms/minimal-pom.xml")

    private val mockClient = HttpClient(MockEngine) {
//        expectSuccess = false
        engine {
            addHandler { request ->
                when (request.url.fullUrl) {
                    "https://repo1.maven.org/maven2/me/lengrand/minimal/1.2/minimal-1.2.pom" -> {
                        respond(minimalResourceStreamPom.readBytes()
                        , headers = headersOf("Content-Type" to listOf(ContentType.Application.Xml.toString())))
                    }
                    "https://repo1.maven.org/maven2/me/lengrand/unknown/1.2/unknown-1.2.pom" -> {
                        respond("", HttpStatusCode.NotFound)
                    }
                    else -> error("Unhandled ${request.url.fullUrl}")
                }
            }
        }
        // TODO : How do I avoid repeating this again ? That's my implementation?!
        install(JsonFeature) {
            serializer = JacksonSerializer(jackson = PomParser.kotlinXmlMapper)
            accept(ContentType.Text.Xml)
            accept(ContentType.Application.Xml)
            accept(ContentType.Text.Plain)
        }
    }

    private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
    private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

    private val mavenClient = MavenClient(mockClient)

    @Test
    fun shouldSuccessOnValidRemotePom(){
        val remoteDependency = POMDependency(
            artifactId = "minimal",
            version = "1.2",
            groupId = "me.lengrand")

        val remotePom = POMProject(
            artifactId = "minimal",
            version = "1.2",
            groupId = "me.lengrand",
            scm = null,
            issueManagement = null)

        runBlocking {
            val clientResult = mavenClient.getDependencyPom(remoteDependency)
            assertTrue(clientResult is MavenClientSuccess)
            assertEquals((clientResult as MavenClientSuccess).pomProject, remotePom)
        }
    }

    @Test
    fun shouldFailureOnUnknownRemotePom(){
        val remoteDependency = POMDependency(
            artifactId = "unknown",
            version = "1.2",
            groupId = "me.lengrand")

        runBlocking {
            val clientResult = mavenClient.getDependencyPom(remoteDependency)
            assertTrue(clientResult is MavenClientFailure)
            assertNotNull((clientResult as MavenClientFailure).throwable)
        }
    }

    @Test
    fun shouldFailureOnUnknownLocalPom(){
        runBlocking {
            val clientResult = mavenClient.getPom(File("unknownFile").absolutePath)
            assertTrue(clientResult is MavenClientFailure)
            assertNotNull((clientResult as MavenClientFailure).throwable)
        }
    }

    @Test
    fun shouldSuccessOnKnownLocalPom(){
        val localPom = POMProject(
            artifactId = "minimal",
            version = "1.2",
            groupId = "me.lengrand",
            scm = null,
            issueManagement = null)

        runBlocking {
            val clientResult = mavenClient.getPom(File(minimalResourcePom.toURI()).absolutePath)
            assertTrue(clientResult is MavenClientSuccess)
            assertEquals((clientResult as MavenClientSuccess).pomProject, localPom)
        }
    }

    @Test
    fun shouldGenerateValidRepoUrlFromProject(){
        val randomPomDependency = POMDependency(
            artifactId = "gson",
            version = "2.8.6",
            groupId = "com.google.code.gson")

        val url = generateUrl(randomPomDependency)
        assertEquals("https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.pom", url)
    }
}