package parsers.maven

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class IssuesExtractorTest {

    private val issuesExtractor = IssuesExtractor()

    @Test
    fun shouldThrowExceptionIfScmAndIssueManagementNull(){

        assertThrows<POMException> { issuesExtractor.getIssuesUrlFromProject(POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = null,
            issueManagement = null
        )) }

        assertThrows<POMException> { issuesExtractor.getIssuesUrlFromProject(POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = null,
            issueManagement = POMIssuesManagement(null)
        )) }

        assertThrows<POMException> { issuesExtractor.getIssuesUrlFromProject(POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = POMScm(null),
            issueManagement = null
        )) }

        assertThrows<POMException> { issuesExtractor.getIssuesUrlFromProject(POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = POMScm(null),
            issueManagement = POMIssuesManagement(null)
        )) }
    }

    @Test
    fun shouldThrowExceptionIfRepoUrlIsNotGithub() {

        assertThrows<POMException> {
            issuesExtractor.getIssuesUrlFromProject(
                POMProject(
                    groupId = "groupId",
                    artifactId = "artifact",
                    version = "version",
                    scm = POMScm("scm:svn:http://java-tuples.svn.sourceforge.net/svnroot/java-tuples/tags/javatuples/javatuples-1.2"),
                    issueManagement = null
                )
            )
        }
    }

    @Test
    fun returnIssueUrlIfPresent() {

        assertEquals("https://github.com/hub4j/github-api/issues",
            issuesExtractor.getIssuesUrlFromProject(
                POMProject(
                    groupId = "groupId",
                    artifactId = "artifact",
                    version = "version",
                    scm = POMScm("https://github.com/hub4j/github-api"),
                    issueManagement = POMIssuesManagement("https://github.com/hub4j/github-api/issues"),
                )
            )
        )
    }

    @Test
    fun returnConvertedScmUrlIfPresent() {

        assertEquals(
            "https://github.com/hub4j/github-api/issues",
            issuesExtractor.getIssuesUrlFromProject(
                POMProject(
                    groupId = "groupId",
                    artifactId = "artifact",
                    version = "version",
                    scm = POMScm("https://github.com/hub4j/github-api"),
                    issueManagement = POMIssuesManagement(null),
                )
            )
        )
    }

    @Test
    fun returnIssueUrlIfBothPresent() {

        assertEquals(
            "https://github.com/hub4j/github-api/issues",
            issuesExtractor.getIssuesUrlFromProject(
                POMProject(
                    groupId = "groupId",
                    artifactId = "artifact",
                    version = "version",
                    scm = POMScm("NOPE"),
                    issueManagement = POMIssuesManagement( "https://github.com/hub4j/github-api/issues"),
                )
            )
        )
    }
}