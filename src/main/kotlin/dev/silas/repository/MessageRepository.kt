package dev.silas.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.silas.db.model.public_.Tables.MESSAGES
import dev.silas.model.Job
import org.jooq.DSLContext
import org.jooq.JSONB
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class MessageRepository(
    private val dsl: DSLContext,
    private val mapper: ObjectMapper
) {

    fun getAll() = dsl.selectFrom(MESSAGES).map {
        Message(
            id = it.id, processed = it.processed, body = mapper.readValue(it.body.data())
        )
    }

    fun addNew(job: Job) = dsl.insertInto(MESSAGES).set(MESSAGES.ID, UUID.randomUUID())
        .set(MESSAGES.BODY, JSONB.valueOf(mapper.writeValueAsString(job)))
        .execute()

    fun getForProcessing(): Message? =
        dsl.selectFrom(MESSAGES)
            .where(MESSAGES.PROCESSED.eq(false))
            .limit(1)
            .forUpdate()
            .noWait()
            .fetchOne()?.map {
                val message = it.into(MESSAGES)
                Message(
                    id = message.id,
                    processed = message.processed,
                    body = mapper.readValue(message.body.data())
                )
            }

    fun updateProcessed(message: Message) {
        dsl.update(MESSAGES)
            .set(MESSAGES.PROCESSED, message.processed)
            .where(MESSAGES.ID.eq(message.id))
            .execute()
    }
}

data class Message(
    val id: UUID,
    val processed: Boolean,
    val body: Job
)
