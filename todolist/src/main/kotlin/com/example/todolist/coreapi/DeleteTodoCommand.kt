package com.example.todolist.coreapi

import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.TargetAggregateIdentifier

class DeleteTodoCommand(@TargetAggregateIdentifier val id: Int) {
    init {
        TodoDeletedEvent(id)
    }
}