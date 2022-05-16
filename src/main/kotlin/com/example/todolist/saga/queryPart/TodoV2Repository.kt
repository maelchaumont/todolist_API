package com.example.todolist.saga.queryPart

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*

interface TodoV2Repository: MongoRepository<TodoV2Repository.TodoV2Deadline, UUID> {
    //No subtask list in TodoV2Deadline view
    @Document(collection = "todoV2Saga")
    data class TodoV2Deadline(
        @MongoId
        val id: UUID,
        var name: String,
        var description: String,
        var priority: String,
        val creationDate: LocalDateTime,
        val minutesBeforeUpdateImpossible: Int,
        val nbUpdates: Int)
}