package com.example.todolist.coreapi.subtask

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo

class SubtaskCreatedEvent(val subtaskCreated: Subtask, val todoToAttach: Todo) {
}