package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo

class SubtasksDeletedFromTodoEvent(val todo: Todo, val subtasksDeleted: List<Subtask>) {
}