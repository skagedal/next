package tech.skagedal.assistant

import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.FileSystem
import java.nio.file.FileSystems

@Configuration
open class Config {
    @Bean
    open fun fileSystem(): FileSystem = FileSystems.getDefault()

    @Bean
    open fun jacksonFactory(): JacksonFactory = JacksonFactory.getDefaultInstance()
}