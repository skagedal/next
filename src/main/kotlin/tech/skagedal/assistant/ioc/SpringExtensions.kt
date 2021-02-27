package tech.skagedal.assistant.ioc

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import tech.skagedal.assistant.Config

inline fun <reified T> ApplicationContext.bean(): T = getBean(T::class.java)

fun createApplicationContext(classInRootPackage: Class<*>): ApplicationContext =
    AnnotationConfigApplicationContext(Config::class.java).apply {
        scan(classInRootPackage.packageName)
        registerShutdownHook()
    }
