package me.lengrand.mygoodfirstissues.parsers.maven

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PomParserTest {

    private val pomParser = PomParser()

    private val examplePom = javaClass.classLoader.getResourceAsStream("poms/example-pom.xml")
    private val javatuplesPom = javaClass.classLoader.getResourceAsStream("poms/javatuples-pom.xml")
    private val gsonPom = javaClass.classLoader.getResourceAsStream("poms/gson-pom.xml")
    private val gsonParentPom = javaClass.classLoader.getResourceAsStream("poms/gson-parent-pom.xml")

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun shouldParsePomInfo(){
        val pomProject = pomParser.parse(examplePom)
        assertEquals("alexa-skills-kit-samples", pomProject.groupId)
        assertEquals("1.0", pomProject.version)
        assertEquals("helloworld", pomProject.artifactId)
    }

    @Test
    fun shouldParsePomDependencies(){
        val pomProject = pomParser.parse(javatuplesPom)
        assertEquals(2, pomProject.dependencies.size)

        assertEquals("commons-lang", pomProject.dependencies[0].groupId)
        assertEquals("2.5", pomProject.dependencies[0].version)
        assertEquals("commons-lang", pomProject.dependencies[0].artifactId)

        assertEquals("junit", pomProject.dependencies[1].groupId)
        assertEquals("4.8.2", pomProject.dependencies[1].version)
        assertEquals("junit", pomProject.dependencies[1].artifactId)
    }

    @Test
    fun shouldParseScmUrl(){
        val pomProject = pomParser.parse(examplePom)
        assertNotNull(pomProject.scm)
        assertEquals("https://github.com/amzn/alexa-skills-kit-java.git", pomProject.scm?.url)
    }

    @Test
    fun shouldParseIssuesManagement(){
        val pomProject = pomParser.parse(gsonParentPom)
        assertNotNull(pomProject.issueManagement)
        assertEquals("https://github.com/google/gson/issues", pomProject.issueManagement?.url)
    }

    @Test
    fun shouldNotFailWhenNoScm(){
        val pomProject = pomParser.parse(gsonPom)
        assertNull(pomProject.scm)
    }

    @Test
    fun shouldNotFailWhenNoIssuesManagement(){
        val pomProject = pomParser.parse(gsonPom)
        assertNull(pomProject.issueManagement)
    }
}