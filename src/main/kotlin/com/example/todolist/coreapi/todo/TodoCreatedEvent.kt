package com.example.todolist.coreapi.todo

import java.util.UUID

data class TodoCreatedEvent(val id: UUID,
                            val name: String,
                            val description: String,
                            val priority: String,
                            val subtasks: List<Subtask>) {
    data class Subtask(val id: UUID, val name: String)
}