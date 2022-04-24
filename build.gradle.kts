
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.revolut.jooq-docker") version "0.3.5"
    id("com.google.cloud.tools.jib") version "3.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq:2.6.7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // database
    implementation("org.jooq:jooq:3.16.5")
    implementation("org.jooq:jooq-meta:3.16.5")
    implementation("org.postgresql:postgresql:42.3.3")

    // json mapper
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    jdbc("org.postgresql:postgresql:42.3.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

val dockerImageVersion by lazy {
    System.getenv()["DOCKER_IMAGE_VERSION"]?.takeUnless { it.isBlank() } ?: project.version.toString()
}

configure<com.google.cloud.tools.jib.gradle.JibExtension> {
    from {
        image = "amazoncorretto:11"
    }
    to {
        image = "blink1982/message-processor:$dockerImageVersion"
    }
}

tasks.withType<com.revolut.jooq.GenerateJooqClassesTask> {
    schemas = arrayOf("public")
    basePackageName = "dev.silas.db.model"
    excludeFlywayTable = true
    customizeGenerator {
        withDatabase(
            org.jooq.meta.jaxb.Database()
                .withIncludes("public.*|pg_catalog.pg_advisory_xact_lock|pg_catalog.pg_try_advisory_lock|pg_catalog.pg_advisory_unlock")
        )
    }
}
