package me.lengrand.mygoodfirstissues.parsers

import me.lengrand.mygoodfirstissues.parsers.gradle.GradleDependency
import me.lengrand.mygoodfirstissues.parsers.gradle.GradleService
import me.lengrand.mygoodfirstissues.parsers.maven.*
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import kotlin.io.path.ExperimentalPathApi

data class LibDependency(val group: String, val name: String, val version: String){
    constructor(dependency: Dependency) : this(dependency.groupId, dependency.artifactId, dependency.version)
    constructor(dependency: GradleDependency) : this(dependency.group, dependency.name, dependency.version)
}

class ParserServiceException(message: String) : Exception(message)

sealed class ParsingResult
data class ParsingFailure(val url : String, val throwable : Throwable) : ParsingResult()
data class ParsingSuccess(
    val url: String,
    val dependencies: List<LibDependency>,
    val githubName: GithubNameResult
) : ParsingResult()

class ParserService @ExperimentalPathApi constructor(
    private val gradleService : GradleService = GradleService(),
    private val mavenService: MavenService = MavenService(MavenService.getDefaultClient(), EffectivePomFetcher()),
    private val githubNameExtractor: GithubNameExtractor = GithubNameExtractor()
) {

    @ExperimentalPathApi
    suspend fun get(urlOrPath: String) : ParsingResult {
        return when{
            urlOrPath.endsWith("pom.xml") || urlOrPath.endsWith(".pom") -> {
                return try{
                    val model = mavenService.getPom(urlOrPath)

                    val deps1 = if(model.dependencies != null) model.dependencies else listOf()
                    val deps2 = if(model.dependencyManagement.dependencies != null) model.dependencyManagement.dependencies else listOf()
                    val dependencies = (deps1 + deps2).map { LibDependency(it) }

                    val githubName = githubNameExtractor.getGithubNameFromProject(model)

                    return ParsingSuccess(urlOrPath, dependencies, githubName)
                }
                catch (e : Exception) { ParsingFailure(urlOrPath, e) }
            }
            urlOrPath.endsWith("build.gradle") -> {

                return try{
                    val dependencies = gradleService.getBuild(urlOrPath)
                    val libDependencies = dependencies.map { LibDependency(it) }

                    return ParsingSuccess(urlOrPath, libDependencies, GithubNameFailure(Model()))
                }
                catch(e: Exception) { ParsingFailure(urlOrPath, e)}

            }
            else -> ParsingFailure(
                urlOrPath,
                ParserServiceException("$urlOrPath is not of a supported filetype. Please pick a pom.xml extension"))
        }
    }
}