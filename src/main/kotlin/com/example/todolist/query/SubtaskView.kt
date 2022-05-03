package com.example.todolist.query

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.EntityId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "subtask")
class SubtaskView constructor(@MongoId @EntityId val subtaskID: String,
                              @Field(name = "name") val name: String) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubtaskView

        if (subtaskID != other.subtaskID) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subtaskID.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Subtask(subtaskID='$subtaskID', name='$name')"
    }
}