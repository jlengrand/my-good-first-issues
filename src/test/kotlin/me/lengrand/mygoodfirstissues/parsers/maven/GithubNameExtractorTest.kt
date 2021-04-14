package me.lengrand.mygoodfirstissues.parsers.maven

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GithubNameExtractorTest {
//
//    private val issuesExtractor = GithubNameExtractor()
//
//    @Test
//    fun shouldThrowExceptionIfScmAndIssueManagementNull(){
//        val pomNoScmNoissue = POMProject(
//            groupId = "groupId",
//            artifactId = "artifact",
//            version = "version",
//            scm = null,
//            issueManagement = null
//        )
//        assertEquals(GithubNameFailure(pomNoScmNoissue), issuesExtractor.getGithubNameFromProject(pomNoScmNoissue) )
//
//        val pomNoScm =  POMProject(
//            groupId = "groupId",
//            artifactId = "artifact",
//            version = "version",
//            scm = null,
//            issueManagement = POMIssuesManagement(null)
//        )
//        assertEquals(GithubNameFailure(pomNoScm), issuesExtractor.getGithubNameFromProject(pomNoScm))
//
//        val pomNullScm = POMProject(
//            groupId = "groupId",
//            artifactId = "artifact",
//            version = "version",
//            scm = POMScm(null),
//            issueManagement = null
//        )
//        assertEquals(GithubNameFailure(pomNullScm), issuesExtractor.getGithubNameFromProject(pomNullScm))
//
//        val pomNullScmNullIssue = POMProject(
//            groupId = "groupId",
//            artifactId = "artifact",
//            version = "version",
//            scm = POMScm(null),
//            issueManagement = POMIssuesManagement(null)
//        )
//        assertEquals(GithubNameFailure(pomNullScmNullIssue), issuesExtractor.getGithubNameFromProject(pomNullScmNullIssue) )
//    }
//
//    @Test
//    fun shouldThrowExceptionIfRepoUrlIsNotGithub() {
//
//        val pom = POMProject(
//            groupId = "groupId",
//            artifactId = "artifact",
//            version = "version",
//            scm = POMScm("scm:svn:http://java-tuples.svn.sourceforge.net/svnroot/java-tuples/tags/javatuples/javatuples-1.2"),
//            issueManagement = null
//        )
//        assertEquals(GithubNameFailure(pom), issuesExtractor.getGithubNameFromProject(pom))
//    }
//
//    @Test
//    fun returnIssueUrlIfPresent() {
//
//        assertEquals(
//            GithubNameSuccess("hub4j/github-api"),
//            issuesExtractor.getGithubNameFromProject(
//                POMProject(
//                    groupId = "groupId",
//                    artifactId = "artifact",
//                    version = "version",
//                    scm = POMScm("https://github.com/hub4j/github-api"),
//                    issueManagement = POMIssuesManagement("https://github.com/hub4j/github-api/issues"),
//                )
//            )
//        )
//    }
//
//    @Test
//    fun returnConvertedScmUrlIfPresent() {
//
//        assertEquals(
//            GithubNameSuccess("hub4j/github-api"),
//            issuesExtractor.getGithubNameFromProject(
//                POMProject(
//                    groupId = "groupId",
//                    artifactId = "artifact",
//                    version = "version",
//                    scm = POMScm("https://github.com/hub4j/github-api"),
//                    issueManagement = POMIssuesManagement(null),
//                )
//            )
//        )
//    }
//
//    @Test
//    fun returnIssueUrlIfBothPresent() {
//
//        assertEquals(
//            GithubNameSuccess("hub4j/github-api"),
//            issuesExtractor.getGithubNameFromProject(
//                POMProject(
//                    groupId = "groupId",
//                    artifactId = "artifact",
//                    version = "version",
//                    scm = POMScm("NOPE"),
//                    issueManagement = POMIssuesManagement( "https://github.com/hub4j/github-api/issues"),
//                )
//            )
//        )
//    }
}