package com.example.todolist.query

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("prout")
interface TodoRepository: MongoRepository<TodoView, UUID> {
}