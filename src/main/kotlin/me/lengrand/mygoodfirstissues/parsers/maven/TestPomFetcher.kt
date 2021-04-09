package me.lengrand.mygoodfirstissues.parsers.maven

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
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

// Inspired from https://stackoverflow.com/questions/55527844/programatically-getting-an-effective-pom-using-maven-resolver-provider
object EffectivePomBuilder {
    @JvmStatic
    fun main(args: Array<String>) {
        val path = Paths.get("./target")
        Files.createDirectories(path)

        val workingDir = path.toString()
        val locator = serviceLocator()
        val system = locator.getService(RepositorySystem::class.java)
        val session = MavenRepositorySystemUtils.newSession()
        val localRepo = LocalRepository("$workingDir/m2")
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepo)
        val remoteRepositoryManager = locator.getService(RemoteRepositoryManager::class.java)

        val repos = listOf(
            RemoteRepository.Builder(
                "central", "default",
                "https://repo.maven.apache.org/maven2/"
            ).build()
        )
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
        val springBootPOMPath = "https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/2.1.4.RELEASE/spring-boot-2.1.4.RELEASE.pom"
        val springBootPOM = downloadPOM(springBootPOMPath, HttpClientBuilder.create().build(), workingDir)

        modelBuildingRequest.pomFile = springBootPOM
        modelBuildingRequest.modelResolver = modelResolver

        val modelBuilder = DefaultModelBuilderFactory().newInstance()
        println(modelBuilder.build(modelBuildingRequest).effectiveModel.dependencies)
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
        val request = HttpGet(pomURL)
        val response = client.execute(request)
        val outputFile = File("$workingDir/" + Paths.get(URI(pomURL).path).fileName.toString())
        println("**** " + outputFile.absolutePath)
        response.entity.content.use { contentStream ->
            Files.copy(contentStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        return outputFile
    }
}