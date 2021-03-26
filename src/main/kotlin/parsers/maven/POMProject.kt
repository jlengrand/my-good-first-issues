package parsers.maven

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

data class POMDependency(

    @set:JsonProperty("groupId")
    var groupId: String,

    @set:JsonProperty("artifactId")
    var artifactId: String,

    @set:JsonProperty("version")
    var version: String
)

@JsonRootName("project")
data class POMProject(

    @set:JsonProperty("groupId")
    var groupId: String?,

    @set:JsonProperty("artifactId")
    var artifactId: String,

    @set:JsonProperty("version")
    var version: String?,

    @set:JsonAlias("dependencies", "dependency")
    var dependencies: List<POMDependency> = ArrayList(),

    @set:JsonAlias("scm", "url")
    var repoUrl : String?,

    @set:JsonAlias("issueManagement", "url")
    var issuesUrl : String?
)