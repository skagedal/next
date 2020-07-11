package tech.skagedal.next.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.Reader
import java.io.StringReader

class ConfigurationLoader {
    private val objectMapper = ObjectMapper(YAMLFactory()).run {
        registerModule(KotlinModule(singletonSupport = SingletonSupport.CANONICALIZE))
    }

    fun loadTasks(reader: Reader) = TasksFile(objectMapper.readValue(reader))
}