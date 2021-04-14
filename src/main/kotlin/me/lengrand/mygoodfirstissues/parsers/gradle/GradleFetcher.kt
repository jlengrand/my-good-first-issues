package me.lengrand.mygoodfirstissues.parsers.gradle

import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.HashMap
import kotlin.io.path.ExperimentalPathApi


// Largely inspired from https://github.com/lovettli/liferay-ide/tree/master/tools/plugins/com.liferay.ide.gradle.core/src/com/liferay/ide/gradle/core/parser
class GradleFetcher() {

    companion object{
        @ExperimentalPathApi
        @JvmStatic
        fun main(args : Array<String>){
            println("test")

            val buildLocation = "/Users/jlengrand/IdeaProjects/my-good-first-issues/src/test/resources/gradleBuilds/swacli-gradle.build"
            val buildLocationFolder = "/Users/jlengrand/IdeaProjects/swacli"

            val file = File(buildLocation)
            val gradleDependencyUpdater =
                GradleDependencyUpdater(file)
            println(gradleDependencyUpdater.dependencies)
            println("done")
        }
    }

}

class FindDependenciesVisitor : CodeVisitorSupport() {
    private var dependenceLineNum = -1
    val dependencies: MutableList<GradleDependency> = ArrayList()

    override fun visitMethodCallExpression(call: MethodCallExpression) {
        if (call.methodAsString != "buildscript") {
            if (call.methodAsString == "dependencies") {
                if (dependenceLineNum == -1) {
                    dependenceLineNum = call.lastLineNumber
                }
            }
            super.visitMethodCallExpression(call)
        }
    }

    override fun visitArgumentlistExpression(ale: ArgumentListExpression) {
        val expressions = ale.expressions
        if (expressions.size == 1 && expressions[0] is ConstantExpression) {
            val depStr = expressions[0].text
            val deps = depStr.split(":").toTypedArray()
            if (deps.size == 3) {
                dependencies.add(GradleDependency(deps[0], deps[1], deps[2]))
            }
        }
        super.visitArgumentlistExpression(ale)
    }

    override fun visitMapExpression(expression: MapExpression) {
        val mapEntryExpressions = expression.mapEntryExpressions
        val dependenceMap: MutableMap<String, String> = HashMap()
        for (mapEntryExpression in mapEntryExpressions) {
            val key = mapEntryExpression.keyExpression.text
            val value = mapEntryExpression.valueExpression.text
            dependenceMap[key] = value
        }
        dependencies.add(GradleDependency(dependenceMap))
        super.visitMapExpression(expression)
    }
}

class GradleDependencyUpdater(scriptContents: String) {
    val dependencies: List<GradleDependency>

    constructor(inputFile: File) : this(IOUtils.toString(FileInputStream(inputFile), StandardCharsets.UTF_8))

    init {
        val builder = AstBuilder()
        val nodes = builder.buildFromString(scriptContents)

        val visitor = FindDependenciesVisitor()
        for (node in nodes) {
            node.visit(visitor)
        }

        dependencies = visitor.dependencies.filter { it.isValid() }
    }
}

data class GradleDependency(val group: String, val name: String, val version: String){
    constructor(dep: Map<String, String>) : this(
        if(dep["group"] != null)  dep["group"]!! else "",
        if(dep["name"] != null)  dep["name"]!! else "",
        if(dep["version"] != null)  dep["version"]!! else ""
    )

    fun isValid() = group.isNotBlank() && name.isNotBlank() && version.isNotBlank()
}