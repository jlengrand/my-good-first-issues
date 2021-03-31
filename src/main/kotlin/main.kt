import github.GitHubService
import github.GithubLogin
import kotlinx.coroutines.runBlocking
import parsers.maven.GithubUrlExtractor
import parsers.maven.MavenClient
import parsers.maven.UrlSuccess
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess


fun main() {
    val githubConfigPath = Paths.get(Paths.get(System.getProperty("user.dir")).toString(), ".githubconfig")
    if (!File(githubConfigPath.toString()).exists()) {
        println("No GitHub config file found, exiting.")
        exitProcess(0)
    }
    val reader = FileReader(githubConfigPath.toFile())
    val properties = Properties()
    properties.load(reader)

    val rawPomUrl = "https://raw.githubusercontent.com/jlengrand/github-templates/main/lambda/pom.xml"

    val mavenClient = MavenClient()
    val issuesUrlExtractor = GithubUrlExtractor()

    // Sample to a list of issues URL from a pom file
    runBlocking{
        val pomProject = mavenClient.getPom(rawPomUrl)
        println(pomProject.dependencies)

        val urls = pomProject.dependencies.map { mavenClient.getDependencyPom(it) }
            .map { issuesUrlExtractor.getGithubNameFromProject(it) }
            .filterIsInstance<UrlSuccess>()

        println(urls)
    }

    // Sample to getGoodFirstIssues from one URL

//    val gitHubService = GitHubService(properties["login"].toString(), properties["oauth"].toString())
    val gitHubService = GitHubService(GithubLogin())

    runBlocking {
        val issues = gitHubService.getGoodIssues("ktorio/ktor")
        println(issues.size)
        println("RTYRTYRTYTRRTRYR")
        println(issues)
    }
}