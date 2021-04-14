package me.lengrand.mygoodfirstissues.parsers.maven

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.cache.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import me.lengrand.mygoodfirstissues.parsers.LibDependency
import me.lengrand.mygoodfirstissues.parsers.maven.PomParser.Companion.kotlinXmlMapper
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.ExperimentalPathApi

sealed class MavenClientResult
data class MavenClientFailure(val url : String, val throwable : Throwable) : MavenClientResult()
data class MavenClientSuccess(val url : String, val pomProject: Model) : MavenClientResult()

@ExperimentalPathApi
class MavenService(private val mavenClient : HttpClient, private val pomFetcher : EffectivePomFetcher) {

    fun getDependencyPom(pomDependency: Dependency) = getPom(generateUrl(pomDependency))

    private fun getRemotePom(url : String) = pomFetcher.getEffectiveModel(url)

    private fun getLocalPom(filePath : String) =
        if (!File(filePath).exists()) throw FileNotFoundException("$filePath was not found on your machine")
        else
            pomFetcher.getEffectiveModel(File(filePath))

    fun getPom(urlOrFilePath: String): Model {
        return if (isRemotePom(urlOrFilePath)) getRemotePom(urlOrFilePath) else getLocalPom(urlOrFilePath)
//        val deps1 = if(project.dependencies != null) project.dependencies else listOf()
//        val deps2 = if(project.dependencyManagement.dependencies != null) project.dependencyManagement.dependencies else listOf()
//        return NameAndDependencies(project.name, deps1 + deps2, project.scm?.url, project.issueManagement?.url)
    }

    private fun isRemotePom(urlOrFilePath : String) =
        urlOrFilePath.startsWith("http://") || urlOrFilePath.startsWith("https://")

    companion object {
        fun getDefaultClient(): HttpClient {
            return HttpClient(Apache) {
                install(HttpCache)
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

fun generateUrl(pomProject: Dependency) = generateUrl(me.lengrand.mygoodfirstissues.parsers.LibDependency(
    group = pomProject.groupId,
    name = pomProject.artifactId,
    version = pomProject.version
))

fun generateUrl(libDependency: LibDependency) : String {
    // TODO Use a URLBuilder
    return ROOT_MAVEN_URL +
            libDependency.group.replace(".", "/") + "/" +
            libDependency.name + "/" +
            libDependency.version + "/" +
            libDependency.name + "-" + libDependency.version + ".pom";
}