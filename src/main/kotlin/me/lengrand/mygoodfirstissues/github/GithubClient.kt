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
import me.lengrand.mygoodfirstissues.parsers.maven.MavenClientException
import me.lengrand.mygoodfirstissues.parsers.maven.MavenClientFailure
import me.lengrand.mygoodfirstissues.parsers.maven.POMProject
import java.util.*

sealed class GitHubServiceResult
data class GitHubServiceFailure(val throwable : Throwable) : GitHubServiceResult()
data class GitHubServiceSuccess(val githubIssues: List<GithubIssue>) : GitHubServiceResult()

class GitHubServiceException( message : String) : Throwable(message)

class GitHubService(githubLogin: GithubLogin) {

    private val helpLabels = listOf("good first issue", "help wanted", "up for grabs") // TODO : Use later

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // TODO : Look into conditional requests https://docs.github.com/en/rest/guides/getting-started-with-the-rest-api#conditional-requests
    private val githubClient = HttpClient(Apache) {
//        install(Logging)
        install(JsonFeature) {
            serializer = JacksonSerializer(mapper)
            accept(ContentType.Application.Json)
        }

        defaultRequest {
            method = HttpMethod.Get
            host = "api.github.com"
            header("Accept", "application/vnd.github.v3+json")
            if (githubLogin.hasToken()) header("Authorization", githubLogin.authToken)
        }
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value > 200) {
                    GitHubServiceFailure(GitHubServiceException(response.toString()))
                }
            }
        }
    }

    suspend fun getUser() : GithubUser = githubClient.get<GithubUser> {
        url {
            encodedPath = "/user"
        }
    }

    suspend fun getGoodIssues(repoName: String) : GitHubServiceResult {

        return GitHubServiceSuccess(githubClient.get<List<GithubIssue>> {
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
}

data class GithubUser(
    @set:JsonProperty("login")
    var login: String,

    @set:JsonProperty("id")
    var id: Int,

    @set:JsonProperty("email")
    var email: String?,
)

data class GithubIssue(
    @set:JsonProperty("id")
    var id: Int,

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
    var updatedAt : Date

)

data class GithubLogin(val login: String? = null, val oauth: String? = null){
    val authToken = if(login == null || oauth == null)  null else "Basic " + Base64.getEncoder().encode("$login.login:$oauth.oauth".toByteArray()).toString(Charsets.UTF_8)

    fun hasToken() =  authToken != null
}