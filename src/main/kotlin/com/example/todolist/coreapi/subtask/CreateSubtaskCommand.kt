package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier

class CreateSubtaskCommand(@TargetAggregateIdentifier val subtaskID: String, val name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateSubtaskCommand

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
        return "CreateSubtaskCommand(subtaskID='$subtaskID', name='$name')"
    }
}