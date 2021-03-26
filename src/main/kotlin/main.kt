
import org.kohsuke.github.GHRepository
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
    val repositories = github.myself.listRepositories()

    println(repositories.mapNotNull { ghRepository -> ghRepository.language }.joinToString(","))

    // Test issues

    val tag = "good+first+issue"
}