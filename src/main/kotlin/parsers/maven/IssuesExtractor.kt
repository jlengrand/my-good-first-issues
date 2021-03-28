package parsers.maven

class IssuesExtractor {

    fun getIssuesUrlFromProject(pomProject: POMProject) : String {
        if(pomProject.issueManagement?.url == null && pomProject.scm?.url == null)
            throw POMException("No valid repository URL found for ${pomProject.groupId}:${pomProject.artifactId}:${pomProject}")

        return if(pomProject.issueManagement?.url != null) pomProject.issueManagement!!.url!!
            else convertToIssueUrl(pomProject.scm!!.url!!)
    }

    private fun convertToIssueUrl(repoUrl: String): String {
        return if (repoUrl.startsWith("https://github.com")) "${repoUrl.replace(".git", "")}/issues" else throw POMException("Not a Github URL : $repoUrl")
    }
}