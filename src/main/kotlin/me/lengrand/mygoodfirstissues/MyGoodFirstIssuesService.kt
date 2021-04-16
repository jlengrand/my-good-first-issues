package me.lengrand.mygoodfirstissues

import me.lengrand.mygoodfirstissues.github.GitHubService
import me.lengrand.mygoodfirstissues.github.GitHubServiceSuccess
import me.lengrand.mygoodfirstissues.github.GithubIssue
import me.lengrand.mygoodfirstissues.github.GithubLogin
import me.lengrand.mygoodfirstissues.logging.AppLogger
import me.lengrand.mygoodfirstissues.logging.SilentAppLogger
import me.lengrand.mygoodfirstissues.parsers.ParserService
import me.lengrand.mygoodfirstissues.parsers.ParsingFailure
import me.lengrand.mygoodfirstissues.parsers.ParsingSuccess
import me.lengrand.mygoodfirstissues.parsers.maven.*
import kotlin.io.path.ExperimentalPathApi

sealed class MyGoodFirstIssuesServiceResult
data class GithubIssuesFailure(val throwable : Throwable) : MyGoodFirstIssuesServiceResult()
data class GithubIssuesSuccess(val githubIssues: List<GithubIssue>) : MyGoodFirstIssuesServiceResult()

@ExperimentalPathApi
class MyGoodFirstIssuesService(
    private val parserService : ParserService = ParserService(),
    private val gitHubService: GitHubService = GitHubService(GitHubService.getDefaultClient(GithubLogin())),
    private val logger : AppLogger = SilentAppLogger()){

    suspend fun getGithubIssues(urlOrPath: String): MyGoodFirstIssuesServiceResult {

        val parsingResult = parserService.get(urlOrPath)
        if(parsingResult is ParsingFailure) {
            logger.logParsingFailure(urlOrPath, parsingResult)
            return GithubIssuesFailure(parsingResult.throwable)
        }

        val dependencies = (parsingResult as ParsingSuccess).dependencies
        logger.logDependencies(dependencies)

        dependencies.forEach { logger.logNewDependency(it) }

        val (parsedDependencies, parsedFailures) = dependencies
            .map { parserService.get(generateUrl(it)) }
            .partition{ it is ParsingSuccess}

        parsedFailures.forEach { logger.logPomDependencyFailure((it as ParsingFailure).url) }

        val (githubNames, githubFailures) = parsedDependencies
            .map{(it as ParsingSuccess).githubName}
            .partition { it is GithubNameSuccess }

        githubFailures.forEach { logger.logGithubFailure(it) }

        val (goodFirstIssues, issueFailures) = githubNames
            .map{(it as GithubNameSuccess).name}
            .flatMap { gitHubService.getGoodIssues(it) }
            .partition { it is GitHubServiceSuccess }

        // TODO : WTF?
//        println(issueFailures)
//        issueFailures.forEach { logger.logGithubIssueFailure(it) }

        return GithubIssuesSuccess(goodFirstIssues.flatMap { (it as GitHubServiceSuccess).githubIssues })
    }
}