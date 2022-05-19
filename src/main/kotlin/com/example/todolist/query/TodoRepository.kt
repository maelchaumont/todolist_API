package com.example.todolist.query

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("prout")
interface TodoRepository: MongoRepository<TodoRepository.TodoView, UUID> {

    @Document(collection = "todolist")
    data class TodoView(@MongoId val id: UUID,
                        @Field(name = "name") var name: String,
                        @Field(name = "description") var description: String,
                        @Field(name = "priority") var priority: String,
                        @Field(name = "subtasks") val subtasks: MutableList<SubtaskView>)

    data class SubtaskView(val subtaskID: UUID, val name: String)
}