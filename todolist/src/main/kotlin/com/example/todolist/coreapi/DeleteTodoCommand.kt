package com.example.todolist.coreapi

import org.axonframework.modelling.command.AggregateIdentifier

class DeleteTodoCommand(@AggregateIdentifier val id: Int) {
}