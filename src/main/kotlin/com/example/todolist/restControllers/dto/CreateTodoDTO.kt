package com.example.todolist.restControllers.dto

data class CreateTodoDTO(val name: String, val description: String, val priority: String, val subtasks: List<Subtask>) {
    data class Subtask(val name: String)
}