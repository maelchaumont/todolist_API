package com.example.todolist.saga.commandPart

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class DeleteTodoV2Command(@TargetAggregateIdentifier val todoV2ToDeleteID: UUID)