package me.lengrand.mygoodfirstissues

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default

class CliFirstGoodIssues : CliktCommand() {
    val pomLocation : String by argument(help="location of the pom scan to scan").default("./pom.xml")

    override fun run() {
        TODO("Not yet implemented")
        echo("Let's find some Open-Source for you to work on!")
    }

}

fun main(args: Array<String>) = CliFirstGoodIssues().main(args)
