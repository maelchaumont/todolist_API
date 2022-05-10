package com.example.todolist.coreapi.subtask

import java.util.*

data class SubtaskDeletedEvent(val subtaskDeletedID: UUID, val todoAttachedID: UUID)