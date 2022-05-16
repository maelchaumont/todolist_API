package com.example.todolist.saga.messagesPart

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateTodoV2PriorityCommand(@TargetAggregateIdentifier val id: UUID, val priority: String?)