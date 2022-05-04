package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

class DeleteSubtaskCommand(@TargetAggregateIdentifier val subtaskToDeleteId: UUID) {
}