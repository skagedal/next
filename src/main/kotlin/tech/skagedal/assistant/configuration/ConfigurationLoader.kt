package tech.skagedal.assistant.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.Reader
import java.lang.RuntimeException

class ConfigurationLoader {
    class BadConfigurationFormat(message: String, cause: Throwable): RuntimeException(message, cause)

    private val objectMapper = ObjectMapper(YAMLFactory()).run {
        registerModule(KotlinModule(singletonSupport = SingletonSupport.CANONICALIZE))
    }

    fun loadTasks(reader: Reader) = try {
        TasksFile(objectMapper.readValue(reader))
    } catch (e: InvalidTypeIdException) {
        throw BadConfigurationFormat(
            "Unknown task: ${e.typeId}",
            e
        )
    } catch (e: UnrecognizedPropertyException) {
        throw BadConfigurationFormat(e.localizedMessage, e)
    }

    internal fun parseWhenExpression(string: String) = objectMapper.readValue<WhenExpression>(string)
}