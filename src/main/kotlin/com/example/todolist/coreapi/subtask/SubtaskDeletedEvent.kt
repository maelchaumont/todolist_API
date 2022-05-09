package com.example.todolist.coreapi.subtask

import com.example.todolist.command.Todo
import java.util.*

class SubtaskDeletedEvent(val subtaskDeletedID: UUID, val todoAttached: Todo) {
}