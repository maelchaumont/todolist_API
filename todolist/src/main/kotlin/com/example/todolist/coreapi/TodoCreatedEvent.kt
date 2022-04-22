package com.example.todolist.coreapi

import com.example.todolist.command.Todo
import org.springframework.boot.actuate.trace.http.HttpTrace.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class TodoCreatedEvent(val theTodo: Todo) {
}