package com.example.todolist.query

import com.example.todolist.command.Todo
import com.example.todolist.coreapi.TodoCreatedEvent
import com.example.todolist.coreapi.TodoDeletedEvent
import com.example.todolist.queryr.FindOneTodoQuery
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.litote.kmongo.KMongo
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.litote.kmongo.service.MongoClientProvider
import org.springframework.boot.actuate.trace.http.HttpTrace.Response
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

class TodoProjection() {
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("todolist_db") //normal java driver usage
    val collectionTodo = database.getCollection<Todo>() //KMongo extension method


    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        collectionTodo.save(todoCreatedEvent.theTodo)
        return ResponseEntity(todoCreatedEvent.theTodo, HttpStatus.CREATED)
    }

    @EventHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        collectionTodo.deleteOne(Todo::id eq todoDeletedEvent.idToDelete)
    }

    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery) {
        /*
        val query = Query()
        query.fields().include("name").exclude("id")
        val myList: List<Todo> = mongoTemplate.find(query, Todo::class.java)
         */
    }

    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery) {}
}