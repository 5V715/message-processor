package dev.silas.processor

import dev.silas.model.Job
import dev.silas.repository.Message
import dev.silas.repository.MessageRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageProcessor(
    private val repository: MessageRepository
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(
        fixedDelayString = "\${processor.interval}",
        initialDelayString = "\${processor.initialDelay}"
    )
    @Transactional
    fun processMessages() {
        when (val message = repository.getForProcessing()) {
            is Message -> {
                logger.info { "starting to work on : ${message.id}" }
                when (message.body) {
                    is Job.SaySomething -> {
                        logger.info { "should say: ${message.body.text}" }
                    }
                    is Job.Add -> {
                        logger.info { "${message.body.left} + ${message.body.right} = ${message.body.left.plus(message.body.right)}" }
                    }
                    is Job.Break -> {
                        logger.info { "will take a break for 10 sec" }
                        Thread.sleep(message.body.seconds.toLong() * 1000)
                    }
                    is Job.Boom -> {
                        logger.error { "throwing error" }
                        throw IllegalStateException(message.body.text)
                    }
                }
                repository.updateProcessed(message.copy(processed = true))
            }
            else -> {
                logger.info { "nothing todo" }
            }
        }
    }
}
