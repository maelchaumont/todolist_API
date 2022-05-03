package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier

class UpdateTodoCommand(val mapThingsToUpdate: Map<String, Any>, @TargetAggregateIdentifier val idTodo: Int) {
}