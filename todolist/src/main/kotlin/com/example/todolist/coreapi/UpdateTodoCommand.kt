package com.example.todolist.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier

class UpdateTodoCommand(val mapThingsToUpdate: Map<String, Any>, @TargetAggregateIdentifier val idTodo: Int) {
}