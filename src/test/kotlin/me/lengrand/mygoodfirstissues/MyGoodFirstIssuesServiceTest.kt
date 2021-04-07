package me.lengrand.mygoodfirstissues

import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.lengrand.mygoodfirstissues.github.GitHubService
import me.lengrand.mygoodfirstissues.github.GitHubServiceFailure
import me.lengrand.mygoodfirstissues.github.GitHubServiceSuccess
import me.lengrand.mygoodfirstissues.github.GithubIssue
import me.lengrand.mygoodfirstissues.parsers.maven.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class MyGoodFirstIssuesServiceTest {

    private val mavenService = mockk<MavenService>()
    private val githubNameExtractor = mockk<GithubNameExtractor>()
    private val githubService = mockk<GitHubService>()

    private val mockPomProject = mockk<POMProject>()

    private val pomUrl = "http://pom.url"

    private val myGoodFirstIssuesService = MyGoodFirstIssuesService(mavenService, githubNameExtractor, githubService)

    private class TestException : Exception()

    @Test
    fun shouldReturnFailureWhenMavenServiceFails() {
        coEvery { mavenService.getPom(pomUrl) } returns MavenClientFailure(TestException())

        runBlocking {
            assertTrue(myGoodFirstIssuesService.getGithubIssues(pomUrl) is GithubIssuesFailure)
            assertTrue((myGoodFirstIssuesService.getGithubIssues(pomUrl) as GithubIssuesFailure).throwable is TestException)
        }

        coVerify { mavenService.getPom(pomUrl) }
    }

    @Test
    fun shouldNotFailWhenNoDependenciesAreFound(){
        coEvery { mavenService.getPom(pomUrl) } returns MavenClientSuccess(mockPomProject)
        every { mockPomProject.dependencies } returns listOf()

        runBlocking {
            assertTrue(myGoodFirstIssuesService.getGithubIssues(pomUrl) is GithubIssuesSuccess)
            assertTrue((myGoodFirstIssuesService.getGithubIssues(pomUrl) as GithubIssuesSuccess).githubIssues.isEmpty())
        }

        coVerify { mavenService.getPom(pomUrl) }
        verify { mockPomProject.dependencies }
        coVerify(inverse = true) { mavenService.getDependencyPom(any())}
    }

    @Test
    fun shouldNotFailWhenNoIssuesAreFound(){
        coEvery { mavenService.getDependencyPom(any()) } returns MavenClientSuccess(mockPomProject)
        coEvery { mavenService.getPom(any()) } returns MavenClientSuccess(mockPomProject)
        every { mockPomProject.dependencies } returns listOf( POMDependency("me.lengrand", "test-repo", "1.0"))
        coEvery { githubNameExtractor.getGithubNameFromProject(any()) } returns GithubNameSuccess("a-test-name")
        coEvery { githubService.getGoodIssues(any()) } returns GitHubServiceSuccess(listOf())

        runBlocking {
            val result = myGoodFirstIssuesService.getGithubIssues(pomUrl)
            assertTrue(result is GithubIssuesSuccess)
            assertTrue((result as GithubIssuesSuccess).githubIssues.isEmpty())
        }

        coVerify(exactly = 1) { mavenService.getDependencyPom(any()) }
        coVerify(exactly = 1) { mavenService.getPom(pomUrl) }
        verify(exactly = 1) { mockPomProject.dependencies }
        coVerify(exactly = 1) { githubService.getGoodIssues("a-test-name") }
    }

    @Test
    fun shouldNotFailWhenIssuesAreFailing(){
        coEvery { mavenService.getDependencyPom(any()) } returns MavenClientSuccess(mockPomProject)
        coEvery { mavenService.getPom(any()) } returns MavenClientSuccess(mockPomProject)
        every { mockPomProject.dependencies } returns listOf( POMDependency("me.lengrand", "test-repo", "1.0"))
        coEvery { githubNameExtractor.getGithubNameFromProject(any()) } returns GithubNameSuccess("a-test-name")
        coEvery { githubService.getGoodIssues(any()) } returns GitHubServiceFailure(TestException())

        runBlocking {
            val result = myGoodFirstIssuesService.getGithubIssues(pomUrl)
            assertTrue(result is GithubIssuesSuccess)
            assertTrue((result as GithubIssuesSuccess).githubIssues.isEmpty())
        }

        coVerify(exactly = 1) { mavenService.getDependencyPom(any()) }
        coVerify(exactly = 1) { mavenService.getPom(pomUrl) }
        verify(exactly = 1) { mockPomProject.dependencies }
        coVerify(exactly = 1) { githubService.getGoodIssues("a-test-name") }
    }

    @Test
    fun shouldReturnIssues(){
        val testIssues = listOf(GithubIssue(
            body = "body",
            htmlUrl = "https://issue.github",
            id = 554,
            title = "title",
            repository_url = "https://repo.github",
            createdAt = Date(),
            updatedAt = Date()
        ))

        coEvery { mavenService.getDependencyPom(any()) } returns MavenClientSuccess(mockPomProject)
        coEvery { mavenService.getPom(any()) } returns MavenClientSuccess(mockPomProject)
        every { mockPomProject.dependencies } returns listOf( POMDependency("me.lengrand", "test-repo", "1.0"))
        coEvery { githubNameExtractor.getGithubNameFromProject(any()) } returns GithubNameSuccess("a-test-name")
        coEvery { githubService.getGoodIssues("a-test-name") } returns GitHubServiceSuccess(
            testIssues
        )

        runBlocking {
            val result = myGoodFirstIssuesService.getGithubIssues(pomUrl)
            assertTrue(result is GithubIssuesSuccess)
            assertTrue((result as GithubIssuesSuccess).githubIssues.size == 1)
            assertEquals((result as GithubIssuesSuccess).githubIssues, testIssues )
        }

        coVerify(exactly = 1) { mavenService.getDependencyPom(any()) }
        coVerify(exactly = 1) { mavenService.getPom(pomUrl) }
        verify(exactly = 1) { mockPomProject.dependencies }
        coVerify(exactly = 1) { githubService.getGoodIssues("a-test-name") }
    }
}