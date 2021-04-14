package me.lengrand.mygoodfirstissues.logging

import me.lengrand.mygoodfirstissues.github.GitHubServiceResult
import me.lengrand.mygoodfirstissues.parsers.LibDependency
import me.lengrand.mygoodfirstissues.parsers.ParsingResult
import me.lengrand.mygoodfirstissues.parsers.maven.GithubNameResult

interface AppLogger{
    fun logNewRepoName(repoName : String)
    fun logNewDependency(pomDependency: LibDependency)
    fun logParsingFailure(urlOrPath: String, pomResult: ParsingResult)
    fun logPomDependencyFailure(url: String)
    fun logGithubFailure(pomProject: GithubNameResult)
    fun logGithubIssueFailure(githubName : String)
    fun logDependencies(dependencies: List<LibDependency>)
    fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>)
}

class SilentAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit
    override fun logNewDependency(pomDependency: LibDependency) = Unit
    override fun logParsingFailure(urlOrPath: String, pomResult: ParsingResult) = Unit
    override fun logPomDependencyFailure(pomDependency: String) = Unit
    override fun logGithubFailure(pomProject: GithubNameResult) = Unit
    override fun logGithubIssueFailure(githubName: String) = Unit
    override fun logDependencies(dependencies: List<LibDependency>) {
        TODO("Not yet implemented")
    }

    override fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>) {
        TODO("Not yet implemented")
    }
}

class DefaultAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit

    override fun logNewDependency(pomDependency: LibDependency) {
        println("Found new dependency : $pomDependency!")
    }

    override fun logParsingFailure(urlOrPath: String, pomResult: ParsingResult) {
        println("Error while fetching and parsing input POM")
    }

    override fun logPomDependencyFailure(pomDependency: String) {
        println("Error while fetching : $pomDependency!")
    }

    override fun logGithubFailure(pomProject: GithubNameResult) {
        println("Wasn't able to find a Github Project name for project $pomProject")
    }

    override fun logGithubIssueFailure(githubName: String) {
        if(githubName.isBlank())
            println("Wasn't able to find Github issues for project $githubName")
    }

    override fun logDependencies(dependencies: List<LibDependency>) {
        TODO("Not yet implemented")
    }

    override fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>) {
        TODO("Not yet implemented")
    }
}