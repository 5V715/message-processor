package dev.silas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MessageProcessorApplication

fun main(args: Array<String>) {
    runApplication<MessageProcessorApplication>(*args)
}
