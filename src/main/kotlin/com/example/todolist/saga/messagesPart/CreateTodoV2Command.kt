package com.example.todolist.saga.messagesPart

import java.time.LocalDateTime

data class CreateTodoV2Command(val name: String,
                               val description: String,
                               val priority: String,
                               val subtasks: List<Subtask>,
                               val creationDate: LocalDateTime) {
    data class Subtask(val name: String)
}