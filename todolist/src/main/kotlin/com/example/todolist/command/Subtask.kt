package com.example.todolist.command

import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import org.axonframework.commandhandling.CommandHandler
import org.springframework.data.annotation.Id


class Subtask() {
    @Id
    var subtaskID: String = ""
    var name: String = ""

    @CommandHandler
    constructor(createSubtaskCommand: CreateSubtaskCommand) : this() {
        this.subtaskID = createSubtaskCommand.subtaskID
        this.name = createSubtaskCommand.name
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
        var result = subtaskID.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Subtask(subtaskID='$subtaskID', name='$name')"
    }
}