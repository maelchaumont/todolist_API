package com.example.todolist.saga.messagesPart

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateTodoV2InfoCommand(@TargetAggregateIdentifier val id: UUID, val name: String?, val description: String?)