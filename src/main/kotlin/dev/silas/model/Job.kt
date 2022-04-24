package dev.silas.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(Job.SaySomething::class, name = "SAY_SOMETHING"),
    JsonSubTypes.Type(Job.Add::class, name = "ADD"),
    JsonSubTypes.Type(Job.Break::class, name = "BREAK"),
    JsonSubTypes.Type(Job.Boom::class, name = "BOOM")
)
sealed class Job(val type: Type) {
    enum class Type {
        SAY_SOMETHING,
        ADD,
        BREAK,
        BOOM
    }

    data class SaySomething(val text: String) : Job(Type.SAY_SOMETHING)

    data class Add(val left: Int, val right: Int) : Job(Type.ADD)

    data class Break(val seconds: Int) : Job(Type.BREAK)

    data class Boom(val text: String) : Job(Type.BOOM)
}
