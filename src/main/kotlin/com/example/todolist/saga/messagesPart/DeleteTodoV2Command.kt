package com.example.todolist.saga.messagesPart

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class DeleteTodoV2Command(@TargetAggregateIdentifier val todoV2ToDeleteID: UUID)