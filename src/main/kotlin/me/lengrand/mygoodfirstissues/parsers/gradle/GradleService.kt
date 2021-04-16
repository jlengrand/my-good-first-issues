package me.lengrand.mygoodfirstissues.parsers.gradle

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.cache.*
import io.ktor.client.request.*
import java.io.File
import java.io.FileNotFoundException

class GradleService(
    private val gradleClient: HttpClient = getDefaultClient()
) {

    private suspend fun getRemoteBuild(urlPath : String) : List<GradleDependency>{
        val zeBuild = gradleClient.get<ByteArray>(urlPath)

        val zeFile = File.createTempFile("temp", ".gradle")
        zeFile.writeBytes(zeBuild)
        return getLocalBuild(zeFile)
    }

    private fun getLocalBuild(file : File) =
        if (!file.exists()) throw FileNotFoundException("${file.path} was not found on your machine")
        else
            GradleDependencyUpdater(file).dependencies

    suspend fun getBuild(urlOrFilePath: String): List<GradleDependency> {
        return if (isRemoteBuild(urlOrFilePath)) getRemoteBuild(urlOrFilePath) else getLocalBuild(File(urlOrFilePath))
    }

    private fun isRemoteBuild(urlOrFilePath : String) =
        urlOrFilePath.startsWith("http://") || urlOrFilePath.startsWith("https://")

    companion object {
        fun getDefaultClient(): HttpClient {
            return HttpClient(Apache) {
                install(HttpCache)
            }
        }
    }
}