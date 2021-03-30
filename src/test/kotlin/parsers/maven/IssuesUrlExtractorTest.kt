package parsers.maven

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class IssuesUrlExtractorTest {

    private val issuesExtractor = IssuesUrlExtractor()

    @Test
    fun shouldThrowExceptionIfScmAndIssueManagementNull(){
        val pomNoScmNoissue = POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = null,
            issueManagement = null
        )
        assertEquals(UrlFailure(pomNoScmNoissue), issuesExtractor.getIssuesUrlFromProject(pomNoScmNoissue) )

        val pomNoScm =  POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = null,
            issueManagement = POMIssuesManagement(null)
        )
        assertEquals(UrlFailure(pomNoScm), issuesExtractor.getIssuesUrlFromProject(pomNoScm))

        val pomNullScm = POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = POMScm(null),
            issueManagement = null
        )
        assertEquals(UrlFailure(pomNullScm), issuesExtractor.getIssuesUrlFromProject(pomNullScm))

        val pomNullScmNullIssue = POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = POMScm(null),
            issueManagement = POMIssuesManagement(null)
        )
        assertEquals(UrlFailure(pomNullScmNullIssue), issuesExtractor.getIssuesUrlFromProject(pomNullScmNullIssue) )
    }

    @Test
    fun shouldThrowExceptionIfRepoUrlIsNotGithub() {

        val pom = POMProject(
            groupId = "groupId",
            artifactId = "artifact",
            version = "version",
            scm = POMScm("scm:svn:http://java-tuples.svn.sourceforge.net/svnroot/java-tuples/tags/javatuples/javatuples-1.2"),
            issueManagement = null
        )
        assertEquals(UrlFailure(pom), issuesExtractor.getIssuesUrlFromProject(pom))
    }

    @Test
    fun returnIssueUrlIfPresent() {

        assertEquals(UrlSuccess("https://github.com/hub4j/github-api/issues"),
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
            UrlSuccess("https://github.com/hub4j/github-api/issues"),
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
            UrlSuccess("https://github.com/hub4j/github-api/issues"),
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