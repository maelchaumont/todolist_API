package com.example.todolist.command

import org.axonframework.modelling.command.EntityId
import java.util.*

class Subtask() {
    @EntityId
    var subtaskID: UUID? = null
    var name: String? = null

    //appelé par SubtaskAndSubtaskViewConverter
    constructor(subtaskID: UUID, name: String): this() {
        this.subtaskID = subtaskID
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subtask

        if (subtaskID != other.subtaskID) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subtaskID?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Subtask(subtaskID=$subtaskID, name=$name)"
    }
}