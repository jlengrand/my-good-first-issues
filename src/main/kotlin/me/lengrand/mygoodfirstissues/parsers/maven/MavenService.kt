package me.lengrand.mygoodfirstissues.parsers.maven

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.lengrand.mygoodfirstissues.parsers.maven.PomParser.Companion.kotlinXmlMapper
import java.io.File
import java.io.FileNotFoundException

sealed class MavenClientResult
data class MavenClientFailure(val throwable : Throwable) : MavenClientResult()
data class MavenClientSuccess(val pomProject: POMProject) : MavenClientResult()

class MavenService(private val mavenClient : HttpClient) {

    suspend fun getDependencyPom(pomDependency: POMDependency) = getPom(generateUrl(pomDependency))

    private suspend fun getRemotePom(url : String) =
        try{ MavenClientSuccess(mavenClient.get<POMProject>(url)) }catch (e: Exception) { MavenClientFailure(e) }

    private fun getLocalPom(filePath : String) =
        if(File(filePath).exists()) (MavenClientSuccess(PomParser().parseFromFilePath(filePath)))
        else MavenClientFailure(FileNotFoundException("$filePath was not found on your machine"))

    suspend fun getPom(urlOrFilePath: String) : MavenClientResult =
        if(isRemotePom(urlOrFilePath)) getRemotePom(urlOrFilePath) else getLocalPom(urlOrFilePath)

    private fun isRemotePom(urlOrFilePath : String) =
        urlOrFilePath.startsWith("http://") || urlOrFilePath.startsWith("https://")

    companion object {
        fun getDefaultClient(): HttpClient {
            return HttpClient(Apache) {
                install(JsonFeature) {
                    serializer = JacksonSerializer(jackson = kotlinXmlMapper)
                    accept(ContentType.Text.Xml)
                    accept(ContentType.Application.Xml)
                    accept(ContentType.Text.Plain)
                }
            }
        }
    }
}

const val ROOT_MAVEN_URL = "https://repo1.maven.org/maven2/";

fun generateUrl(pomProject: POMDependency) : String {
    // TODO Use a URLBuilder
    return ROOT_MAVEN_URL +
            pomProject.groupId.replace(".", "/") + "/" +
            pomProject.artifactId + "/" +
            pomProject.version + "/" +
            pomProject.artifactId + "-" + pomProject.version + ".pom";
}