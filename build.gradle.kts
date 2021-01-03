import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    kotlin("jvm") version "1.4.0"
    id("io.micronaut.library") version "1.1.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.8")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.8")
    implementation("com.google.api-client", "google-api-client", "1.23.0")
    implementation("com.google.oauth-client", "google-oauth-client-jetty", "1.23.0")
    implementation("com.google.apis", "google-api-services-gmail", "v1-rev83-1.23.0")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.0")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.11.0")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.11.0")
    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("com.github.ajalt", "clikt", "2.4.0")
    implementation("de.codeshelf.consoleui", "consoleui", "0.0.13")

    testImplementation("org.jetbrains.kotlin", "kotlin-test")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.0")
}

application {
    mainClassName = "tech.skagedal.assistant.MainKt"
}

micronaut {
    version("2.2.2")
}
ssk
tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                TestLogEvent.STARTED,
                TestLogEvent.PASSED,
                TestLogEvent.FAILED
            )
            // show standard out and standard error of the test
            // JVM(s) on the console
            showStandardStreams = true
        }
    }
}

java {
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}
