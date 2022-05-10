package com.example.todolist.coreapi.todo

data class CreateTodoCommand(val name: String,
                             val description: String,
                             val priority: String,
                             val subtasks: List<Subtask>) {
    data class Subtask(val name: String)
}