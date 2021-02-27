package tech.skagedal.assistant.ioc

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Component
@Qualifier("subcommand")
annotation class Subcommand {
}