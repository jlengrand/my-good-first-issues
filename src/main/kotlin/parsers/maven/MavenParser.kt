package parsers.maven

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class MavenParser(){
    companion object{
        internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
//        setDefaultUseWrapper(false)
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        internal inline fun <reified T: Any> parseAs(path: String) : T {
            val resource = javaClass.classLoader.getResource(path)
            return kotlinXmlMapper.readValue(resource)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("TEST")
            val pomFile = parseAs<POMProject>("poms/example-pom.xml")
            pomFile.dependencies.forEach { println(it) }
            pomFile.dependencies.forEach { println(generateUrl(it)) }

            // Get Url
            val url = "https://repo1.maven.org/maven2/org/javatuples/javatuples/1.2/javatuples-1.2.pom"


        }
    }
}

const val ROOT_MAVEN_URL = "https://repo1.maven.org/maven2/";

fun generateUrl(pomProject: POMDependency) : String {
    return ROOT_MAVEN_URL +
            pomProject.groupId!!.replace(".", "/") + "/" +
            pomProject!!.artifactId + "/" +
            pomProject!!.version + "/" +
            pomProject!!.artifactId + "-" + pomProject!!.version + ".pom";
}


data class POMFile(val libId: LibId, val libConfig: LibConfig?, val libIssue: LibIssue)

data class LibId(val groupId : String, val artifactId: String, val version : String)
data class LibConfig(val connection : String, val url: String)
data class LibIssue(val system : String, val url: String)