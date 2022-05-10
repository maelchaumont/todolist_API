package com.example.todolist.coreapi.todo

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class DeleteTodoCommand(@TargetAggregateIdentifier val id: UUID)