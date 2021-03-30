package parsers.maven

sealed class UrlResult
data class UrlSuccess(val url : String) : UrlResult()
data class UrlFailure(val pomProject: POMProject) : UrlResult()

class IssuesUrlExtractor {

    fun getIssuesUrlFromProject(pomProject: POMProject) : UrlResult {
        if(pomProject.issueManagement?.url == null && pomProject.scm?.url == null)
            return UrlFailure(pomProject)

        return if(pomProject.issueManagement?.url != null) UrlSuccess(pomProject.issueManagement!!.url!!)
            else convertScmToIssueUrl(pomProject)
    }

    private fun convertScmToIssueUrl(pomProject: POMProject): UrlResult {
        val repoUrl = pomProject.scm!!.url!!
        return if (repoUrl.startsWith("https://github.com")) UrlSuccess("${repoUrl.replace(".git", "")}/issues") else UrlFailure(pomProject)
    }
}