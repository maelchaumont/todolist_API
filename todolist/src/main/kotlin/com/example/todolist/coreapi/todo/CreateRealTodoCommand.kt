package com.example.todolist.coreapi.todo

import com.example.todolist.command.Subtask

class CreateRealTodoCommand(val id: Int, val name: String, val description: String, val priority: String, val subtasks: MutableList<Subtask>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateRealTodoCommand

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (priority != other.priority) return false
        if (subtasks != other.subtasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + subtasks.hashCode()
        return result
    }

    override fun toString(): String {
        return "CreateRealTodoCommand(id=$id, name='$name', description='$description', priority='$priority', subtasks=$subtasks)"
    }
}