package me.lengrand.mygoodfirstissues.parsers.maven

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.maven.model.Model
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.resolution.ModelResolver
import org.apache.maven.project.ProjectBuildingRequest
import org.apache.maven.project.ProjectModelResolver
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RequestTrace
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.impl.RemoteRepositoryManager
import org.eclipse.aether.internal.impl.DefaultRepositorySystem
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.transport.wagon.WagonTransporterFactory
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory

// Inspired from https://stackoverflow.com/questions/55527844/programatically-getting-an-effective-pom-using-maven-resolver-provider
@ExperimentalPathApi
class EffectivePomFetcher {

    private val repos = listOf(
        RemoteRepository.Builder(
            "central", "default",
            "https://repo.maven.apache.org/maven2/"
        ).build()
    )

    private val tempPath = createTempDirectory("mavenUrl")

    init{
        tempPath.toFile().deleteOnExit() // TODO : Will that ever be run on a website :). Need to build a cache
    }

    @ExperimentalPathApi
    fun getEffectiveModel(url : String): Model {
        val startingPom = downloadPOM(url, HttpClientBuilder.create().build(), tempPath.toString()) // TODO : Do async
        return getEffectiveModel(startingPom)
    }

    @ExperimentalPathApi
    fun getEffectiveModel(startingPom: File): Model {

        val locator = serviceLocator()
        val system = locator.getService(RepositorySystem::class.java)
        val session = MavenRepositorySystemUtils.newSession()
        session.localRepositoryManager = system.newLocalRepositoryManager(session, LocalRepository("$tempPath/m2"))
        val remoteRepositoryManager = locator.getService(RemoteRepositoryManager::class.java)

        val repositorySystem = DefaultRepositorySystem()
        repositorySystem.initService(locator)

        val modelResolver: ModelResolver = ProjectModelResolver(
            session,
            RequestTrace(null),
            repositorySystem,
            remoteRepositoryManager,
            repos,
            ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT,
            null
        )

        val modelBuildingRequest = DefaultModelBuildingRequest()
        modelBuildingRequest.pomFile = startingPom
        modelBuildingRequest.modelResolver = modelResolver

        val model =  DefaultModelBuilderFactory().newInstance().build(modelBuildingRequest).effectiveModel
        return model
    }

    private fun serviceLocator(): DefaultServiceLocator {
        val locator = MavenRepositorySystemUtils.newServiceLocator()
        locator.addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
        locator.addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
        locator.addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        locator.addService(TransporterFactory::class.java, WagonTransporterFactory::class.java)
        return locator
    }

    private fun downloadPOM(pomURL: String, client: HttpClient, workingDir: String): File {
        val response = client.execute(HttpGet(pomURL))
        val outputFile = File("$workingDir/" + Paths.get(URI(pomURL).path).fileName.toString())
        response.entity.content.use { contentStream ->
            Files.copy(contentStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        return outputFile
    }
}