package me.lengrand.mygoodfirstissues

import me.lengrand.GitHubService
import me.lengrand.GithubIssue
import me.lengrand.GithubLogin
import me.lengrand.mygoodfirstissues.parsers.maven.GithubUrlExtractor
import me.lengrand.mygoodfirstissues.parsers.maven.MavenClient
import me.lengrand.mygoodfirstissues.parsers.maven.UrlSuccess

class MyGoodFirstIssuesService {

    private val mavenClient = MavenClient()
    private val githubUrlExtractor = GithubUrlExtractor()
    private val gitHubService = GitHubService(GithubLogin())

    suspend fun getGithubIssues(urlOrPath: String): List<GithubIssue> {
        val pomProject = mavenClient.getPom(urlOrPath)

        val githubNames = pomProject.dependencies.map { mavenClient.getDependencyPom(it) }
            .map { githubUrlExtractor.getGithubNameFromProject(it) }
            .filterIsInstance<UrlSuccess>()

        // TODO: Also mention all the failures
        // TODO : Handle Github refusals

        return githubNames.flatMap { gitHubService.getGoodIssues("ktorio/ktor") }
    }
}