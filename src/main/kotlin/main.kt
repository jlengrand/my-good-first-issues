
import org.kohsuke.github.GitHubBuilder
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("Hello World!")

    val githubConfigPath = Paths.get(Paths.get(System.getProperty("user.dir")).toString(), ".githubconfig")
    if (!File(githubConfigPath.toString()).exists()) {
        println("No GitHub config file found, exiting.")
        exitProcess(0)
    }

    val github = GitHubBuilder.fromPropertyFile(githubConfigPath.toString()).build()
    val results = github.myself.listRepositories()

    println(results.mapNotNull { ghRepository -> ghRepository.language }.joinToString(","))
}