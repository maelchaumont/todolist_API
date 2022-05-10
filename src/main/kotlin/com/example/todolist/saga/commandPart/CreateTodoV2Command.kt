package com.example.todolist.saga.commandPart

import java.util.*

data class CreateTodoV2Command(val name: String,
                               val description: String,
                               val priority: String,
                               val subtasks: List<Subtask>,
                               val timeLeftTodo: Int) {
    data class Subtask(val id: UUID, val name: String)
}