package me.lengrand.mygoodfirstissues

import io.ktor.utils.io.*
import me.lengrand.mygoodfirstissues.github.GitHubService
import me.lengrand.mygoodfirstissues.github.GitHubServiceSuccess
import me.lengrand.mygoodfirstissues.github.GithubIssue
import me.lengrand.mygoodfirstissues.github.GithubLogin
import me.lengrand.mygoodfirstissues.logging.AppLogger
import me.lengrand.mygoodfirstissues.logging.SilentAppLogger
import me.lengrand.mygoodfirstissues.parsers.maven.*
import kotlin.io.path.ExperimentalPathApi

sealed class MyGoodFirstIssuesServiceResult
data class GithubIssuesFailure(val throwable : Throwable) : MyGoodFirstIssuesServiceResult()
data class GithubIssuesSuccess(val githubIssues: List<GithubIssue>) : MyGoodFirstIssuesServiceResult()

@ExperimentalPathApi
class MyGoodFirstIssuesService(
    private val mavenService: MavenService = MavenService(MavenService.getDefaultClient(), EffectivePomFetcher()),
    private val githubNameExtractor: GithubNameExtractor = GithubNameExtractor(),
    private val gitHubService: GitHubService = GitHubService(GitHubService.getDefaultClient(GithubLogin())),
    private val logger : AppLogger = SilentAppLogger()){

    suspend fun getGithubIssues(urlOrPath: String): MyGoodFirstIssuesServiceResult {
        val pomResult = mavenService.getPom(urlOrPath)

        if(pomResult is MavenClientFailure) {
            logger.logPomFailure(urlOrPath, pomResult)
            return GithubIssuesFailure(pomResult.throwable)
        }

        val dependencies = (pomResult as MavenClientSuccess).pomProject.dependencies +
                pomResult.pomProject.dependencyManagement.dependencies
        logger.logDependencies(dependencies)

        dependencies.forEach { logger.logNewDependency(it) }

        val (dependencyPoms, dependencyFailures) = dependencies.map { Pair(it, mavenService.getDependencyPom(it)) }
            .partition { it.second is MavenClientSuccess }

        dependencyFailures.forEach { logger.logPomDependencyFailure(it.first) }

        val (githubNames, githubFailures) = dependencyPoms
            .map{(it.second as MavenClientSuccess).pomProject}
            .map { Pair(it, githubNameExtractor.getGithubNameFromProject(it)) }
            .partition { it.second is GithubNameSuccess }

        githubFailures.forEach { logger.logGithubFailure(it.first) }

        val (goodFirstIssues, issueFailures) = githubNames
            .map{(it.second as GithubNameSuccess).name}
            .map { Pair(it, gitHubService.getGoodIssues(it)) }
            .partition { it.second is GitHubServiceSuccess }

        // TODO : WTF?
        println(issueFailures)
        issueFailures.forEach { logger.logGithubIssueFailure(it.first) }

        // TODO : Avoid duplicates

        return GithubIssuesSuccess(goodFirstIssues.flatMap { (it.second as GitHubServiceSuccess).githubIssues })
    }
}