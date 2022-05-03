package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo

class SubtasksAddedToTodoEvent(val todo: Todo, val subtasksAdded: List<Subtask>) {
}