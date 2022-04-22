package com.example.todolist.coreapi

import org.axonframework.modelling.command.AggregateIdentifier

class CreateRealTodoCommand(@AggregateIdentifier val id: Int, val name: String, val description: String, val priority: String) {
}