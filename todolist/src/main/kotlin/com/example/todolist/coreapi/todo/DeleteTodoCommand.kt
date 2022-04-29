package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier

class DeleteTodoCommand(@TargetAggregateIdentifier val id: Int) {
    init {
        TodoDeletedEvent(id)
    }
}