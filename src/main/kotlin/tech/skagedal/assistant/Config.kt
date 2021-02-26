package tech.skagedal.assistant

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.FileSystem
import java.nio.file.FileSystems

@Configuration
open class Config {
    @Bean
    open fun fileSystem(): FileSystem = FileSystems.getDefault()
}