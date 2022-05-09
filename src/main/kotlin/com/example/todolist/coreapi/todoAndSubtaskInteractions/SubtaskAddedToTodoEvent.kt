package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo

class SubtaskAddedToTodoEvent(val todo: Todo, val subtaskAdded: Subtask) {
}