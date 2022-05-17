package com.example.todolist.saga.messagesPart

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class AddToPercentageDoneTodoV2SagaCommand(@TargetAggregateIdentifier val id: UUID, val newPercentage: Int)