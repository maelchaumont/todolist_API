package com.example.todolist.restControllers.dto

import java.util.*

data class CreateTodoDTO(val name: String, val description: String, val priority: String, val subtasks: List<Subtask>) {
    data class Subtask(val subtaskID: UUID, val name: String)// UUID à enlever
}