package me.lengrand.mygoodfirstissues.logging

import me.lengrand.mygoodfirstissues.parsers.maven.MavenClientFailure
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model

interface AppLogger{
    fun logNewRepoName(repoName : String)
    fun logNewDependency(pomDependency: Dependency)
    fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure)
    fun logPomDependencyFailure(pomDependency: Dependency)
    fun logGithubFailure(pomProject: Model)
    fun logGithubIssueFailure(githubName : String)
}

class SilentAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit
    override fun logNewDependency(pomDependency: Dependency) = Unit
    override fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure) = Unit
    override fun logPomDependencyFailure(pomDependency: Dependency) = Unit
    override fun logGithubFailure(pomProject: Model) = Unit
    override fun logGithubIssueFailure(githubName: String) = Unit
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

    override fun logGithubFailure(pomProject: Model) {
        println("Wasn't able to find a Github Project name for project $pomProject")
    }

    override fun logGithubIssueFailure(githubName: String) {
        println("Wasn't able to find Github issues for project $githubName")
    }
}