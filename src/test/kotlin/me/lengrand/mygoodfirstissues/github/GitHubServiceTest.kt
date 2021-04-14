package me.lengrand.mygoodfirstissues.github

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import me.lengrand.mygoodfirstissues.GithubIssuesFailure
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GitHubServiceTest{
//
//    private val incorrectRepoName = "unknown/github"
//    private val correctRepoName = "known/github"
//
//    private val githubServiceResult = listOf(GithubIssue(
//            body = "body",
//            htmlUrl = "https://issue.github",
//            id = 554,
//            nodeId = 54567,
//            title = "title",
//            repository_url = "https://repo.github",
//            createdAt = Date(),
//            updatedAt = Date(),
//            repository = GithubRepository(
//                id = 321,
//                nodeId = 245234,
//                name = "randomUser/randomName",
//                fullName = "a random name",
//                repoUrl = "https://repo.github",
//            )
//        ))
//
//    private val mapper = ObjectMapper().registerKotlinModule().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//
//    private val mockClient = HttpClient(MockEngine) {
//        install(JsonFeature) {
//            serializer = JacksonSerializer(mapper)
//            accept(ContentType.Application.Json)
//        }
//        defaultRequest {
//            method = HttpMethod.Get
//            host = "api.github.com"
//            header("Accept", "application/vnd.github.v3+json")
//            if (GithubLogin().hasToken()) header("Authorization", GithubLogin().authToken)
//        }
//
//        engine {
//            addHandler { request ->
//                when (request.url.fullUrl) {
//                    "http://api.github.com/repos/${incorrectRepoName}/issues?labels=help+wanted&state=open&assignee=none" -> {
//                        respond("", HttpStatusCode.NotFound)
//                    }
//                    "http://api.github.com/repos/${correctRepoName}/issues?labels=help+wanted&state=open&assignee=none" -> {
//                        respond(
//                            mapper.writeValueAsString(githubServiceResult),
//                            headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
//                        )
//                    }
//                    else -> error("Unhandled ${request.url.fullUrl}")
//                }
//            }
//        }
//    }
//
//    private val gitHubService = GitHubService(mockClient)
//
//    @Test
//    fun incorrectUrlShouldReturnFailure(){
//        runBlocking {
//            val result = gitHubService.getGoodIssues(incorrectRepoName)
//            assertTrue { result is GitHubServiceFailure }
//            assertTrue { (result as GitHubServiceFailure).throwable is ClientRequestException }
//        }
//    }
//
//    @Test
//    fun correctUrlShouldReturnGithubInfo(){
//        runBlocking {
//            val result = gitHubService.getGoodIssues(correctRepoName)
//            assertTrue { result is GitHubServiceSuccess }
//            assertEquals(result, GitHubServiceSuccess(githubServiceResult))
//        }
//    }
}

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"