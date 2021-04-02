package me.lengrand.mygoodfirstissues.parsers.maven

sealed class UrlResult
data class UrlSuccess(val url : String) : UrlResult()
data class UrlFailure(val pomProject: POMProject) : UrlResult()

class GithubUrlExtractor {

    fun getGithubNameFromProject(pomProject: POMProject) : UrlResult {
        if(pomProject.issueManagement?.url == null && pomProject.scm?.url == null)
            return UrlFailure(pomProject)

        return if(pomProject.issueManagement?.url != null)
                convertUrlToGithubName(pomProject, pomProject.issueManagement!!.url!!)
            else
                convertUrlToGithubName(pomProject, pomProject.scm!!.url!!)
    }

    private fun convertUrlToGithubName(pomProject: POMProject, url: String): UrlResult {
        return if (url.startsWith("https://github.com"))
            UrlSuccess("${url
                .replace("https://github.com/", "")
                .replace(".git", "")
                .replace("/issues", "")}")
        else UrlFailure(pomProject)
    }
}