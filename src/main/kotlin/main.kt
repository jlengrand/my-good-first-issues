import kotlinx.coroutines.runBlocking
import parsers.maven.IssuesUrlExtractor
import parsers.maven.MavenClient
import parsers.maven.UrlSuccess

fun main(args: Array<String>) {
    println("Hello World!")

    val rawPomUrl = "https://raw.githubusercontent.com/jlengrand/github-templates/main/lambda/pom.xml"

    val mavenClient = MavenClient()
    val issuesUrlExtractor = IssuesUrlExtractor()

    runBlocking{
        val pomProject = mavenClient.getPom(rawPomUrl)
        println(pomProject.dependencies)

        val urls = pomProject.dependencies.map { mavenClient.getDependencyPom(it) }
            .map { issuesUrlExtractor.getIssuesUrlFromProject(it) }
            .filterIsInstance<UrlSuccess>()

        println(urls)
    }
}