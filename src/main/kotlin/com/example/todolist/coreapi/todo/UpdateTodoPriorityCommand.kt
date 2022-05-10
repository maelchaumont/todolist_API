package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateTodoPriorityCommand(@TargetAggregateIdentifier val id: UUID, val priority: String)