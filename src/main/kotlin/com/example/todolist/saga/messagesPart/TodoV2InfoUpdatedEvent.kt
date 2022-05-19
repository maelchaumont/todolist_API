package com.example.todolist.saga.messagesPart

import java.util.*

data class TodoV2InfoUpdatedEvent(val id: UUID, val name: String, val description: String)