package github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.util.*

data class GithubUser(
    @set:JsonProperty("login")
    var login: String,

    @set:JsonProperty("id")
    var id: Int,

    @set:JsonProperty("email")
    var email: String?,
)

class GitHubService(login : String, oauth : String) {
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val authToken = "Basic " + Base64.getEncoder().encode("$login:$oauth".toByteArray()).toString(Charsets.UTF_8)

    private val githubClient = HttpClient(Apache) {
        install(Logging)
        install(JsonFeature) {
            serializer = JacksonSerializer(mapper)
            accept(ContentType.Application.Json)
        }
    }

    fun getUser() {
        runBlocking {
            val user = githubClient.get<GithubUser>("https://api.github.com/user"){
                header("Accept", "application/vnd.github.v3+json")
                header("Authorization", authToken)
            }

            println(user)
        }
    }
}


//val authToken = "Basic " + Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charsets.UTF_8)
//val httpClient = OkHttpClient.Builder()
//    .addInterceptor { chain ->
//        val original = chain.request()
//        val builder = original.newBuilder()
//            .header("Accept", "application/vnd.github.v3+json")
//            .header("Authorization", authToken)
//        val request = builder.build()
//        chain.proceed(request)
//    }
//    .build()
//
//val contentType = "application/json".toMediaType()
//val retrofit = Retrofit.Builder()
//    .baseUrl("https://api.github.com")
//    .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
//    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//    .client(httpClient)
//    .build()
//return retrofit.create(GitHubService::class.java)