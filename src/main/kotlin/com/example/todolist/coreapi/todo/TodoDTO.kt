package com.example.todolist.coreapi.todo

import java.util.UUID

/**
 * Representation of a todo for query responses
 */
data class TodoDTO(
        val id: UUID,
        val name: String,
        val description: String,
        val priority: String,
        val subtasks: List<Subtask>,
) {
    data class Subtask(val id: UUID, val name: String)
}