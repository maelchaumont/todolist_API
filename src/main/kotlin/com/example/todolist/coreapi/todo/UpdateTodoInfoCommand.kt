package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class UpdateTodoInfoCommand(@TargetAggregateIdentifier val id: UUID,
                                 val name: String?,
                                 val description: String?)