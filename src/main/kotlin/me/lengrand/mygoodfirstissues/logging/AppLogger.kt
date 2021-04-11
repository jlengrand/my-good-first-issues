package me.lengrand.mygoodfirstissues.logging

import me.lengrand.mygoodfirstissues.github.GitHubServiceResult
import me.lengrand.mygoodfirstissues.parsers.maven.GithubNameResult
import me.lengrand.mygoodfirstissues.parsers.maven.MavenClientFailure
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model

interface AppLogger{
    fun logNewRepoName(repoName : String)
    fun logNewDependency(pomDependency: Dependency)
    fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure)
    fun logPomDependencyFailure(pomDependency: Dependency)
    fun logGithubFailure(pomProject: GithubNameResult)
    fun logGithubIssueFailure(githubName : String)
    fun logDependencies(dependencies: List<Dependency>)
    fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>)
}

class SilentAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit
    override fun logNewDependency(pomDependency: Dependency) = Unit
    override fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure) = Unit
    override fun logPomDependencyFailure(pomDependency: Dependency) = Unit
    override fun logGithubFailure(pomProject: GithubNameResult) = Unit
    override fun logGithubIssueFailure(githubName: String) = Unit
    override fun logDependencies(dependencies: List<Dependency>) {
        TODO("Not yet implemented")
    }

    override fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>) {
        TODO("Not yet implemented")
    }
}

class DefaultAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit

    override fun logNewDependency(pomDependency: Dependency) {
        println("Found new dependency : $pomDependency!")
    }

    override fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure) {
        println("Error while fetching and parsing input POM")
    }

    override fun logPomDependencyFailure(pomDependency: Dependency) {
        println("Error while fetching : $pomDependency!")
    }

    override fun logGithubFailure(pomProject: GithubNameResult) {
        println("Wasn't able to find a Github Project name for project $pomProject")
    }

    override fun logGithubIssueFailure(githubName: String) {
        if(githubName.isBlank())
            println("Wasn't able to find Github issues for project $githubName")
    }

    override fun logDependencies(dependencies: List<Dependency>) {
        TODO("Not yet implemented")
    }

    override fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>) {
        TODO("Not yet implemented")
    }
}