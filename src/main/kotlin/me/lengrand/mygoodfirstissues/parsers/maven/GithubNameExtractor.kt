package me.lengrand.mygoodfirstissues.parsers.maven

sealed class GithubNameResult
data class GithubNameSuccess(val name : String) : GithubNameResult()
data class GithubNameFailure(val pomProject: POMProject) : GithubNameResult()

class GithubNameExtractor {

    fun getGithubNameFromProject(pomProject: POMProject) : GithubNameResult {
        if(pomProject.issueManagement?.url == null && pomProject.scm?.url == null)
            return GithubNameFailure(pomProject)

        return if(pomProject.issueManagement?.url != null)
                convertUrlToGithubName(pomProject, pomProject.issueManagement!!.url!!)
            else
                convertUrlToGithubName(pomProject, pomProject.scm!!.url!!)
    }

    private fun convertUrlToGithubName(pomProject: POMProject, url: String): GithubNameResult {
        // TODO : So not proud of this
        return if (url.startsWith("https://github.com"))
            GithubNameSuccess(
                url
                    .replace("https://github.com/", "")
                    .replace(".git", "")
                    .replace("/issues", "")
                    .trimEnd { it == "/".single() }
            )
        else GithubNameFailure(pomProject)
    }
}