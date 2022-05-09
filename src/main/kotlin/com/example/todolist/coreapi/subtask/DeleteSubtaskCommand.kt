package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

class DeleteSubtaskCommand(val subtaskToDeleteID: UUID, @TargetAggregateIdentifier val todoAttachedID: UUID) {
}