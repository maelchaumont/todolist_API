package com.example.todolist.coreapi.todo

import java.util.*

data class TodoPriorityUpdatedEvent(val id: UUID, val priority: String)