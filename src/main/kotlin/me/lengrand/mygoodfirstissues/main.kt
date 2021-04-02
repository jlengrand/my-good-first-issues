package me.lengrand.mygoodfirstissues

import kotlinx.coroutines.runBlocking
import me.lengrand.GitHubService
import me.lengrand.GithubLogin
import me.lengrand.mygoodfirstissues.parsers.maven.UrlSuccess
import java.util.*

fun main() {
    // TODO : Implement that stuff
    //    val githubConfigPath = Paths.get(Paths.get(System.getProperty("user.dir")).toString(), ".githubconfig")
    //    if (!File(githubConfigPath.toString()).exists()) {
    //        println("No GitHub config file found, exiting.")
    //        exitProcess(0)
    //    }
    //    val reader = FileReader(githubConfigPath.toFile())
    //    val properties = Properties()
    //    properties.load(reader)
    //    val gitHubService = GitHubService(properties["login"].toString(), properties["oauth"].toString())

    val rawPomUrl = "https://raw.githubusercontent.com/jlengrand/github-templates/main/lambda/pom.xml"
    val myGoodFirstIssuesService = MyGoodFirstIssuesService()

    runBlocking {
        val issues = myGoodFirstIssuesService.getGithubIssues(rawPomUrl)
        println(issues.size)
    }
}