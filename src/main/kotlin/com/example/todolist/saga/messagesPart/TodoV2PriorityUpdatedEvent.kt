package com.example.todolist.saga.messagesPart

import java.util.*

data class TodoV2PriorityUpdatedEvent(val id: UUID, val priority: String)