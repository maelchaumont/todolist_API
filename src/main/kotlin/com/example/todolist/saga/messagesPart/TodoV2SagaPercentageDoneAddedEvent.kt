package com.example.todolist.saga.messagesPart

import java.util.*

data class TodoV2SagaPercentageDoneAddedEvent(val id: UUID, val newPercentage: Int)