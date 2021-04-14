package me.lengrand.mygoodfirstissues

import kotlinx.coroutines.runBlocking
import me.lengrand.mygoodfirstissues.github.*
import me.lengrand.mygoodfirstissues.logging.AppLogger
import me.lengrand.mygoodfirstissues.parsers.LibDependency
import me.lengrand.mygoodfirstissues.parsers.ParsingResult
import me.lengrand.mygoodfirstissues.parsers.maven.GithubNameResult
import picocli.CommandLine
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Callable
import kotlin.io.path.ExperimentalPathApi
import kotlin.system.exitProcess

@ExperimentalPathApi
class CliFirstGoodIssues : Callable<Int> {

    @CommandLine.Parameters(index = "0", description = ["location of the pom scan to scan"], defaultValue = "./pom.xml")
    private var pomLocation : String? = null

    override fun call() : Int {
        println(CommandLine.Help.Ansi.AUTO.string("@|bold Let's find some Open-Source for you to work on|@"))
        val myGoodFirstIssuesService = MyGoodFirstIssuesService(
            gitHubService = GitHubService(GitHubService.getDefaultClient(getGithubLogin())),
            logger = PicoCliLogger()
        )

        return runBlocking {
            when(val result = myGoodFirstIssuesService.getGithubIssues(pomLocation!!)){
                is GithubIssuesFailure ->
                    println(result.throwable.message)
                is GithubIssuesSuccess -> {
                    if (result.githubIssues.isEmpty())
                        println(CommandLine.Help.Ansi.AUTO.string("@|yellow,bold Found no issues for you to work on! Try again later! |@"))
                    else
                        prettyPrintIssues(result.githubIssues)
                }
            }
            return@runBlocking 0
        }
    }

    private fun prettyPrintIssues(githubIssues: List<GithubIssue>) {
        println(CommandLine.Help.Ansi.AUTO.string("@|bold,green ======= |@"))
        println(CommandLine.Help.Ansi.AUTO.string("@|bold,green Found ${githubIssues.size} issues for you! |@"))

        githubIssues
            .distinctBy { it.htmlUrl }
            .sortedByDescending { it.createdAt }
            .forEach { prettyPrintIssue(it) }

    }

    private fun getGithubLogin() : GithubLogin{
        val githubConfigPath = Paths.get(Paths.get(System.getProperty("user.dir")).toString(), ".githubconfig")
        if (!File(githubConfigPath.toString()).exists()) {
            println("No GitHub config file found, using without rate limits.")
            return GithubLogin()
        }
        println("I found a Github Login. Let's go!")
        val reader = FileReader(githubConfigPath.toFile())
        val properties = Properties()
        properties.load(reader)

        return GithubLogin(properties["login"].toString(), properties["oauth"].toString())
    }

    private fun prettyPrintIssue(githubIssue: GithubIssue) {
        println(CommandLine.Help.Ansi.AUTO.string(""))
        println(CommandLine.Help.Ansi.AUTO.string("@|bold,green  ${githubIssue.title}|@"))
        println(CommandLine.Help.Ansi.AUTO.string("@|blue  ${githubIssue.htmlUrl} - ${githubIssue.createdAt} |@"))
        println(CommandLine.Help.Ansi.AUTO.string("@|yellow  ${githubIssue.labels.joinToString(",") { it.name }}|@"))
    }

    companion object{
        @JvmStatic
        fun main(args : Array<String>){
            exitProcess(CommandLine(CliFirstGoodIssues()).execute(*args))
        }
    }
}

class PicoCliLogger : AppLogger {

    override fun logNewRepoName(repoName: String) = Unit

    override fun logNewDependency(pomDependency: LibDependency) {
//        println(CommandLine.Help.Ansi.AUTO.string("@|green Found new dependency : $pomDependency! |@"))
    }

    override fun logParsingFailure(urlOrPath: String, pomResult: ParsingResult) {
//        println(CommandLine.Help.Ansi.AUTO.string("@|red Error while fetching and parsing input POM |@"))
    }

    override fun logPomDependencyFailure(url: String) {
//        println(CommandLine.Help.Ansi.AUTO.string("@|red Error while fetching : $pomDependency! |@"))
    }

    override fun logGithubFailure(pomProject: GithubNameResult) {
//        println(CommandLine.Help.Ansi.AUTO.string("@|red Wasn't able to find a Github Project name for project $pomProject |@"))
    }

    override fun logGithubIssueFailure(githubName: String) {
//            println(CommandLine.Help.Ansi.AUTO.string("@| Wasn't able to find Github issues for project $githubName |@"))
    }

    override fun logDependencies(dependencies: List<LibDependency>) {
        println(CommandLine.Help.Ansi.AUTO.string("@|green Found ${dependencies.size} dependencies in the project. Investigating... |@"))
    }

    override fun logIssues(goodFirstIssues: List<Pair<String, GitHubServiceResult>>) {
        goodFirstIssues.forEach{
            println(CommandLine.Help.Ansi.AUTO.string(
                "@|green Found ${(it.second as GitHubServiceSuccess).githubIssues.size} good first issues in project ${it.first} |@"
            ))
        }
    }
}