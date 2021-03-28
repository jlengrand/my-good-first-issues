package parsers.maven

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import parsers.maven.PomParser.Companion.kotlinXmlMapper

class MavenClient {

    private val client = HttpClient(Apache){
        install(Logging)
        install(JsonFeature){
            serializer = JacksonSerializer(jackson = kotlinXmlMapper)
            accept(ContentType.Text.Xml)
        }
    }

    suspend fun getDependencyPom(pomDependency: POMDependency) : POMProject {
        val url = generateUrl(pomDependency)
        return client.get<POMProject>(url)
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