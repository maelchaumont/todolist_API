package com.example.todolist.coreapi.todo

import java.util.*

data class TodoInfoUpdatedEvent(val id: UUID, val name: String?, val description: String?)