package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import org.axonframework.modelling.command.TargetAggregateIdentifier

class AddSubtasksToTodosCommand(@TargetAggregateIdentifier val idTodo: Int, val subtasksToAdd: List<Subtask>) {
}