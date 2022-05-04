package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

class AddSubtasksToTodosCommand(@TargetAggregateIdentifier val idTodo: UUID, val subtasksToAdd: List<Subtask>) {
}