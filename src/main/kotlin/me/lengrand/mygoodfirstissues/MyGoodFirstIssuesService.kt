package me.lengrand.mygoodfirstissues

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

class MyGoodFirstIssuesException(message: String) : Exception(message)

@ExperimentalPathApi
class MyGoodFirstIssuesService(
    private val mavenService: MavenService = MavenService(MavenService.getDefaultClient(), EffectivePomFetcher()),
    private val githubNameExtractor: GithubNameExtractor = GithubNameExtractor(),
    private val gitHubService: GitHubService = GitHubService(GitHubService.getDefaultClient(GithubLogin())),
    private val logger : AppLogger = SilentAppLogger()){

    suspend fun getGithubIssues(urlOrPath: String): MyGoodFirstIssuesServiceResult {
        if(!isSupportedFile(urlOrPath))
            return GithubIssuesFailure(MyGoodFirstIssuesException("$urlOrPath is not of a supported filetype. Please pick a pom.xml extension"))

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
            .map { githubNameExtractor.getGithubNameFromProject(it) }
            .partition { it is GithubNameSuccess }

        githubFailures.forEach { logger.logGithubFailure(it) }

        val (goodFirstIssues, issueFailures) = githubNames
            .map{(it as GithubNameSuccess).name}
            .flatMap { gitHubService.getGoodIssues(it) }
            .partition { it is GitHubServiceSuccess }


        // TODO : WTF?
//        println(issueFailures)
//        issueFailures.forEach { logger.logGithubIssueFailure(it) }

        // TODO : Avoid duplicates

        return GithubIssuesSuccess(goodFirstIssues.flatMap { (it as GitHubServiceSuccess).githubIssues })
    }

    private fun isSupportedFile(urlOrPath: String) = urlOrPath.endsWith("pom.xml")
}