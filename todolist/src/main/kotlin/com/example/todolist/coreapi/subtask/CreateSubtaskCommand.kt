package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier

class CreateSubtaskCommand(@TargetAggregateIdentifier val subtaskID: String, val name: String) {
}