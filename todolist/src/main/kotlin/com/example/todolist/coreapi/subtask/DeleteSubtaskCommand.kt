package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier

class DeleteSubtaskCommand(@TargetAggregateIdentifier val subtaskToDeleteId: String) {
}