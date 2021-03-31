import github.GitHubService
import parsers.maven.IssuesUrlExtractor
import parsers.maven.MavenClient
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    println("Hello World!")

    val rawPomUrl = "https://raw.githubusercontent.com/jlengrand/github-templates/main/lambda/pom.xml"

    val mavenClient = MavenClient()
    val issuesUrlExtractor = IssuesUrlExtractor()

    // Sample to a list of issues URL from a pom file
//    runBlocking{
//        val pomProject = mavenClient.getPom(rawPomUrl)
//        println(pomProject.dependencies)
//
//        val urls = pomProject.dependencies.map { mavenClient.getDependencyPom(it) }
//            .map { issuesUrlExtractor.getIssuesUrlFromProject(it) }
//            .filterIsInstance<UrlSuccess>()
//
//        println(urls)
//    }

    // Sample to getGoodFirstIssues from one URL
    val githubConfigPath = Paths.get(Paths.get(System.getProperty("user.dir")).toString(), ".githubconfig")
    if (!File(githubConfigPath.toString()).exists()) {
        println("No GitHub config file found, exiting.")
        exitProcess(0)
    }
    val reader = FileReader(githubConfigPath.toFile())
    val properties = Properties()
    properties.load(reader)


    val gitHubService = GitHubService(properties["login"].toString(), properties["oauth"].toString())
    gitHubService.getUser()
}