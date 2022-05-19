package com.example.todolist.coreapi.subtask

import java.util.*

data class SubtaskCreatedEvent (val idSubtask: UUID, val name: String, val idTodoAttached: UUID)