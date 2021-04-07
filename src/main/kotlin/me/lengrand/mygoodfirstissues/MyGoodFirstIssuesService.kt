package me.lengrand.mygoodfirstissues

import me.lengrand.mygoodfirstissues.github.GitHubService
import me.lengrand.mygoodfirstissues.github.GitHubServiceSuccess
import me.lengrand.mygoodfirstissues.github.GithubIssue
import me.lengrand.mygoodfirstissues.github.GithubLogin
import me.lengrand.mygoodfirstissues.parsers.maven.*

sealed class MyGoodFirstIssuesServiceResult
data class GithubIssuesFailure(val throwable : Throwable) : MyGoodFirstIssuesServiceResult()
data class GithubIssuesSuccess(val githubIssues: List<GithubIssue>) : MyGoodFirstIssuesServiceResult()

class MyGoodFirstIssuesService(
    private val mavenService: MavenService = MavenService(MavenService.getDefaultClient()),
    private val githubNameExtractor: GithubNameExtractor = GithubNameExtractor(),
    private val gitHubService: GitHubService = GitHubService(GitHubService.getDefaultClient(GithubLogin()))){

    suspend fun getGithubIssues(urlOrPath: String): MyGoodFirstIssuesServiceResult {
        val pomResult = mavenService.getPom(urlOrPath)

        if(pomResult is MavenClientFailure) return GithubIssuesFailure(pomResult.throwable)

        val githubNames = (pomResult as MavenClientSuccess).pomProject.dependencies
            .map { mavenService.getDependencyPom(it) }
            .filterIsInstance<MavenClientSuccess>()
            .map { githubNameExtractor.getGithubNameFromProject(it.pomProject); }
            .filterIsInstance<GithubNameSuccess>()
            .map { it.name }

        // TODO: Also mention all the failures
        // TODO : Handle Github refusals (quota)

        val goodFirstIssues = githubNames
            .map { gitHubService.getGoodIssues(it) }
            .filterIsInstance<GitHubServiceSuccess>()
            .flatMap { it.githubIssues }

        return GithubIssuesSuccess(goodFirstIssues)
    }
}