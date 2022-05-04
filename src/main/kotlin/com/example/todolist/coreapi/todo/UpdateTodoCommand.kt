package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

class UpdateTodoCommand(val mapThingsToUpdate: Map<String, Any>, @TargetAggregateIdentifier val idTodo: UUID) {
}