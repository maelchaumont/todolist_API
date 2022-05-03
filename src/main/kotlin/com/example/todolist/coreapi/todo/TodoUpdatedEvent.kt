package com.example.todolist.coreapi.todo

import com.example.todolist.command.Todo
import org.axonframework.serialization.Revision

@Revision("1")
class TodoUpdatedEvent(val todoUpdated: Todo) {
}