package me.lengrand.mygoodfirstissues.logging

import me.lengrand.mygoodfirstissues.parsers.maven.MavenClientFailure
import me.lengrand.mygoodfirstissues.parsers.maven.POMDependency
import me.lengrand.mygoodfirstissues.parsers.maven.POMProject

interface AppLogger{
    fun logNewRepoName(repoName : String)
    fun logNewDependency(pomDependency: POMDependency)
    fun logNewIssue()
    fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure)
    fun logPomDependencyFailure(pomDependency: POMDependency)
    fun logGithubFailure(pomProject : POMProject)
    fun logGithubIssueFailure(githubName : String)
}

class SilentAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit
    override fun logNewDependency(pomDependency: POMDependency) = Unit
    override fun logNewIssue() = Unit
    override fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure) = Unit
    override fun logPomDependencyFailure(pomDependency: POMDependency) = Unit
    override fun logGithubFailure(pomProject: POMProject) = Unit
    override fun logGithubIssueFailure(githubName: String) = Unit
}

class DefaultAppLogger : AppLogger {
    override fun logNewRepoName(repoName: String) = Unit

    override fun logNewDependency(pomDependency: POMDependency) {
        println("Found new dependency : $pomDependency!")
    }

    override fun logNewIssue() = Unit

    override fun logPomFailure(urlOrPath: String, pomResult: MavenClientFailure) {
        println("Error while fetching and parsing input POM")
    }

    override fun logPomDependencyFailure(pomDependency: POMDependency) {
        println("Error while fetching : $pomDependency!")
    }

    override fun logGithubFailure(pomProject: POMProject) {
        println("Wasn't able to find a Github Project name for project $pomProject")
    }

    override fun logGithubIssueFailure(githubName: String) {
        println("Wasn't able to find Github issues for project $githubName")
    }
}