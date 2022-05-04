package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

class DeleteSubtasksFromTodosCommand(@TargetAggregateIdentifier val idTodo: UUID, val subtasksToDelete: List<Subtask>) {
}