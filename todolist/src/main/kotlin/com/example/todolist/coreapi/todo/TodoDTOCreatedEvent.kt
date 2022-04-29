package com.example.todolist.coreapi.todo

import com.example.todolist.command.TodoNoIdDTO
import org.axonframework.serialization.Revision

@Revision("1")
class TodoDTOCreatedEvent(val theTodoDTO: TodoNoIdDTO) {
}