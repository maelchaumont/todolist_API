package com.example.todolist.coreapi.todoAndSubtaskInteractions

import com.example.todolist.command.Subtask
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

class AddSubtaskToTodoCommand(@TargetAggregateIdentifier val idTodo: UUID, val subtaskToAdd: Subtask) {
}