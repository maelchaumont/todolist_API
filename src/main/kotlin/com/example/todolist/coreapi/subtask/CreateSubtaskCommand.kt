package com.example.todolist.coreapi.subtask

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateSubtaskCommand(val name: String, @TargetAggregateIdentifier val todoAttachedID: UUID)