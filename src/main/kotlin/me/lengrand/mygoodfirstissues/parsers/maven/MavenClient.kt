package me.lengrand.mygoodfirstissues.parsers.maven

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.lengrand.mygoodfirstissues.parsers.maven.PomParser.Companion.kotlinXmlMapper

class MavenClient {

    private val client = HttpClient(Apache){
        install(Logging)
        install(JsonFeature){
            serializer = JacksonSerializer(jackson = kotlinXmlMapper)
            accept(ContentType.Text.Xml)
            accept(ContentType.Application.Xml)
            accept(ContentType.Text.Plain)
        }
    }

    suspend fun getDependencyPom(pomDependency: POMDependency) : POMProject {
        val url = generateUrl(pomDependency)
        return getPom(url)
    }

    private suspend fun getRemotePom(url : String) = client.get<POMProject>(url)

    private fun getLocalPom(filePath : String) =  PomParser().parseFromFilePath(filePath)

    suspend fun getPom(urlOrFilePath: String) =
        if(isRemotePom(urlOrFilePath)) getRemotePom(urlOrFilePath) else getLocalPom(urlOrFilePath)


    private fun isRemotePom(urlOrFilePath : String) =
        urlOrFilePath.startsWith("http://") || urlOrFilePath.startsWith("https://")
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