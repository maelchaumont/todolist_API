package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import org.axonframework.modelling.command.TargetAggregateIdentifier

class DeleteSubtasksFromTodosCommand(@TargetAggregateIdentifier val idTodo: Int, val subtasksToDelete: List<Subtask>) {
}