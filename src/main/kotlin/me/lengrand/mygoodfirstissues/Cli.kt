package me.lengrand.mygoodfirstissues

import kotlinx.coroutines.runBlocking
import me.lengrand.mygoodfirstissues.github.GithubIssue
import me.lengrand.mygoodfirstissues.logging.DefaultAppLogger
import picocli.CommandLine
import java.util.concurrent.Callable
import kotlin.system.exitProcess

class CliFirstGoodIssues : Callable<Int> {

    @CommandLine.Parameters(index = "0", description = ["location of the pom scan to scan"], defaultValue = "./pom.xml")
    private var pomLocation : String? = null

    override fun call() : Int {
        println(CommandLine.Help.Ansi.AUTO.string("@|bold Let's find some Open-Source for you to work on|@"))
        val myGoodFirstIssuesService = MyGoodFirstIssuesService(logger = DefaultAppLogger())

        return runBlocking {
            when(val result = myGoodFirstIssuesService.getGithubIssues(pomLocation!!)){
                is GithubIssuesFailure ->
                    println(result.throwable.message)
                is GithubIssuesSuccess -> {
                    println("Found no issues for you to work on. Everything is clean :)")
                    result.githubIssues.forEach { prettyPrintIssue(it) }
                }
            }
            return@runBlocking 0
        }
    }

    private fun prettyPrintIssue(githubIssue: GithubIssue) =
        println(CommandLine.Help.Ansi.AUTO.string("@|bold,yellow  ${githubIssue.title}|@"))

    companion object{
        @JvmStatic
        fun main(args : Array<String>){
            exitProcess(CommandLine(CliFirstGoodIssues()).execute(*args))
        }
    }
}