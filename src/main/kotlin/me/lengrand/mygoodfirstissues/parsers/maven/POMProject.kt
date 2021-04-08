package me.lengrand.mygoodfirstissues.parsers.maven

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

data class POMDependency(

    @set:JsonProperty("groupId")
    var groupId: String,

    @set:JsonProperty("artifactId")
    var artifactId: String,

    @set:JsonProperty("version")
    var version: String?
){
    override fun toString() = "$groupId:$artifactId:$version"
}

data class POMScm(
    @set:JsonProperty("url")
    var url: String?,
)

data class POMIssuesManagement(
    @set:JsonProperty("url")
    var url: String?,
)

@JsonRootName("project")
data class POMProject(

    @set:JsonProperty("groupId")
    var groupId: String?,

    @set:JsonProperty("artifactId")
    var artifactId: String,

    @set:JsonProperty("version")
    var version: String?,

    @set:JsonAlias("dependency")
    var dependencies: List<POMDependency> = ArrayList(),

    @set:JsonAlias("scm")
    var scm: POMScm?,

    @set:JsonAlias("issueManagement")
    var issueManagement: POMIssuesManagement?
){
    override fun toString() = "$groupId:$artifactId:$version:${scm?.url}:${issueManagement?.url}"
}