package com.example.todolist.query

import com.example.todolist.coreapi.TodoCreatedEvent
import com.example.todolist.coreapi.TodoDeletedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class TodoProjection(@Autowired val todoRepository: TodoRepository) {
    /*
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("todolist_db") //normal java driver usage
    val collectionTodo = database.getCollection<Todo>() //KMongo extension method
     */


    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        //collectionTodo.save(Document())
        //mongoTemplate().save(todoCreatedEvent.theTodo)
        //myRepository.save(todoCreatedEvent.theTodo)
        return ResponseEntity(todoCreatedEvent.theTodo, HttpStatus.CREATED)
    }


    @EventHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        //collectionTodo.deleteOne(Todo::id eq todoDeletedEvent.idToDelete)
    }

    /*
    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery): List<Todo> {
        val query = Query()
        query.fields().include("name", "description", "priority")
        return mongoTemplate().find(query, Todo::class.java)
        //return collectionTodo.find(DefaultClientSession(), Todo::class.java)
    }

     */

    /*
    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): Document? {
        //return collectionTodo.findOne { Todo::id eq findOneTodoQuery.id }
        //val query = Query().addCriteria(Criteria.where("id").isEqualTo(findOneTodoQuery.id))
        //query.fields().include("name", "description", "priority")
        return mongoTemplate().find(query, Todo::class.java)
    }
     */
}
