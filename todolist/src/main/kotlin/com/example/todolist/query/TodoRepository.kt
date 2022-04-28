package com.example.todolist.query

import com.example.todolist.command.Todo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository("prout")
interface TodoRepository: MongoRepository<Todo, Int> {
}