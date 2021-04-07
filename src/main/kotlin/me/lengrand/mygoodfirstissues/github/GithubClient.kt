package me.lengrand.mygoodfirstissues.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

sealed class GitHubServiceResult
data class GitHubServiceFailure(val throwable : Throwable) : GitHubServiceResult()
data class GitHubServiceSuccess(val githubIssues: List<GithubIssue>) : GitHubServiceResult()

class GitHubService(private val githubClient : HttpClient) {

    private val helpLabels = listOf("good first issue", "help wanted", "up for grabs") // TODO : Use later

    companion object{
        // TODO : Look into conditional requests https://docs.github.com/en/rest/guides/getting-started-with-the-rest-api#conditional-requests
        fun getDefaultClient(githubLogin: GithubLogin) = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = JacksonSerializer(ObjectMapper().registerKotlinModule().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
                accept(ContentType.Application.Json)
            }

            defaultRequest {
                method = HttpMethod.Get
                host = "api.github.com"
                header("Accept", "application/vnd.github.v3+json")
                if (githubLogin.hasToken()) header("Authorization", githubLogin.authToken)
            }
        }
    }

    suspend fun getGoodIssues(repoName: String) =
        try{
            GitHubServiceSuccess(githubClient.get<List<GithubIssue>> {
                url {
                    encodedPath = "/repos/${repoName}/issues"
                    parameter(
                        "labels",
                        "help wanted"
                    ) // TODO : Multiple issues. Multiple requests? The default aggregator is an AND
                    parameter("state", "open")
                    parameter("assignee", "none")
                    // default result of 100 is enough for now
                }
            })
        }
        catch (e: Exception) { GitHubServiceFailure(e) }
}

data class GithubIssue(
    @set:JsonProperty("id")
    var id: Int,

    @set:JsonProperty("node_id")
    var nodeId: Int,

    @set:JsonProperty("repository_url")
    var repository_url: String,

    @set:JsonProperty("html_url")
    var htmlUrl: String,

    @set:JsonProperty("title")
    var title: String,

    @set:JsonProperty("body")
    var body: String,

    @set:JsonProperty("created_at")
    var createdAt: Date,

    @set:JsonProperty("updated_at")
    var updatedAt : Date,

    @set:JsonProperty("repository")
    var repository : GithubRepository
)

data class GithubRepository(
    @set:JsonProperty("id")
    var id: Int,

    @set:JsonProperty("node_id")
    var nodeId: Int,

    @set:JsonProperty("name")
    var name: String,

    @set:JsonProperty("full_name")
    var fullName: String,

    @set:JsonProperty("repos_url")
    var repoUrl: String,
)

data class GithubLogin(val login: String? = null, val oauth: String? = null){
    val authToken = if(login == null || oauth == null)  null else "Basic " + Base64.getEncoder().encode("$login.login:$oauth.oauth".toByteArray()).toString(Charsets.UTF_8)

    fun hasToken() =  authToken != null
}