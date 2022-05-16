package com.example.todolist.saga.messagesPart

import java.time.LocalDateTime
import java.util.*

data class TodoV2CreatedEvent(val id: UUID,
                             val name: String,
                             val description: String,
                             val priority: String,
                             val subtasks: List<Subtask>,
                             val creationDate: LocalDateTime,
                             val minutesLeftToUpdate: Int) {
    data class Subtask(val id: UUID, val name: String)
}