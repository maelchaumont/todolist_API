package com.example.todolist.query

import com.example.todolist.command.Todo
import com.example.todolist.coreapi.TodoCreatedEvent
import com.example.todolist.coreapi.TodoDeletedEvent
import com.example.todolist.mongoTemplate
import com.example.todolist.queryr.FindOneTodoQuery
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableAsync

class TodoProjection() {
    /*
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("todolist_db") //normal java driver usage
    val collectionTodo = database.getCollection<Todo>() //KMongo extension method
     */
    val collectionTodo = mongoTemplate().getCollection("todolist")
    val myRepository : TodoRepositoryImpl

    init {
        myRepository = TodoRepositoryImpl()
    }

    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        //collectionTodo.save(todoCreatedEvent.theTodo)
        mongoTemplate().save(todoCreatedEvent.theTodo)
        //myRepository.save(todoCreatedEvent.theTodo)
        return ResponseEntity(todoCreatedEvent.theTodo, HttpStatus.CREATED)
    }

    @EventHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        collectionTodo.deleteOne(Todo::id eq todoDeletedEvent.idToDelete)
    }

    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery): MutableList<Todo> {
        val query = Query()
        query.fields().include("name", "description", "priority")
        return mongoTemplate().find(query, Todo::class.java)
    }

    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): Document? {
        return collectionTodo.findOne { Todo::id eq findOneTodoQuery.id }
        //val query = Query().addCriteria(Criteria.where("id").isEqualTo(findOneTodoQuery.id))
        //query.fields().include("name", "description", "priority")
        //return mongoTemplate().find(query, Todo::class.java)
    }
}
