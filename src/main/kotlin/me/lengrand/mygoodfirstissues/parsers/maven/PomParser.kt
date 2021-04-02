package me.lengrand.mygoodfirstissues.parsers.maven

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.InputStream

class PomParser{

    companion object{
        val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)!!
    }

    fun parse(stream : InputStream): POMProject {
        return kotlinXmlMapper.readValue<POMProject>(stream)
    }

    fun parseFromFilePath(path : String) : POMProject {
        return parse(File(path).inputStream())
    }


//        private inline fun <reified T: Any> parseAs(path: String) : T {
//            val resource = javaClass.classLoader.getResource(path)
//            return kotlinXmlMapper.readValue(resource)
//        }

}