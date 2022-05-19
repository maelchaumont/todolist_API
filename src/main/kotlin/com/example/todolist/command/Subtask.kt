package com.example.todolist.command

import org.axonframework.modelling.command.EntityId
import java.util.*

/**
 * Command class representing a Subtask. Should only be present in Todo aggregate
 *
 *  subtaskID : ID of this Subtask
 *  name : name of this Subtask
 */
class Subtask() {
    @EntityId
    lateinit var subtaskID: UUID
    lateinit var name: String

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

    override fun toString(): String {
        return "Subtask(subtaskID=$subtaskID, name=$name)"
    }

    override fun hashCode(): Int {
        var result = subtaskID.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}