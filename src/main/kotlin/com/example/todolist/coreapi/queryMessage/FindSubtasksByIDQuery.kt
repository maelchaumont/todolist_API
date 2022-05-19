package com.example.todolist.coreapi.queryMessage

import java.util.*

data class FindSubtasksByIDQuery(val subtasksIDs: List<UUID>)