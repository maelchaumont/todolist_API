package com.example.todolist.coreapi.subtask

import java.util.*

data class SubtaskCreatedEvent (val subtask: Subtask, val idTodoAttached: UUID) {
    data class Subtask (val idSubtask: UUID, val name: String)
}