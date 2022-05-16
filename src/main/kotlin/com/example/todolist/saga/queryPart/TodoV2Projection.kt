package com.example.todolist.saga.queryPart

import com.example.todolist.saga.messagesPart.FindAllTodosV2Query
import com.example.todolist.saga.messagesPart.TodoV2CreatedEvent
import com.example.todolist.saga.messagesPart.TodoV2InfoUpdatedEvent
import com.example.todolist.saga.messagesPart.TodoV2PriorityUpdatedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.random.Random

@Component
data class TodoV2Projection( @Autowired val todoV2Repository: TodoV2Repository, @Autowired val mongoTemplate: MongoTemplate) {

    @EventHandler
    fun on(todoV2CreatedEvent: TodoV2CreatedEvent) {
        mongoTemplate.save(TodoV2Repository.TodoV2Deadline(
            todoV2CreatedEvent.id,
            todoV2CreatedEvent.name,
            todoV2CreatedEvent.description,
            todoV2CreatedEvent.priority,
            todoV2CreatedEvent.creationDate,
            Random.nextInt(10),
            0
        ), "todoV2Saga")
    }

    @EventHandler
    fun on(todoV2InfoUpdatedEvent: TodoV2InfoUpdatedEvent) {
        val myTodoV2View = mongoTemplate.findById<TodoV2Repository.TodoV2Deadline>(todoV2InfoUpdatedEvent.id, "todoV2Saga")
        if(LocalDateTime.now() <= myTodoV2View?.creationDate?.plusMinutes(myTodoV2View.minutesBeforeUpdateImpossible.toLong())) {
            if(!todoV2InfoUpdatedEvent.name.isNullOrBlank())
                myTodoV2View?.name = todoV2InfoUpdatedEvent.name
            if(!todoV2InfoUpdatedEvent.description.isNullOrBlank())
                myTodoV2View?.description = todoV2InfoUpdatedEvent.description
            mongoTemplate.save(myTodoV2View!!, "todoV2Saga")
        }
    }

    @EventHandler
    fun on(todoV2PriorityUpdatedEvent: TodoV2PriorityUpdatedEvent) {
        val myTodoV2View = mongoTemplate.findById<TodoV2Repository.TodoV2Deadline>(todoV2PriorityUpdatedEvent.id, "todoV2Saga")
        if(LocalDateTime.now() <= myTodoV2View?.creationDate?.plusMinutes(myTodoV2View.minutesBeforeUpdateImpossible.toLong())) {
            if(!todoV2PriorityUpdatedEvent.priority.isNullOrBlank())
                myTodoV2View?.priority = todoV2PriorityUpdatedEvent.priority
            mongoTemplate.save(myTodoV2View!!, "todoV2Saga")
        }
    }


    @QueryHandler
    fun handle(findAllTodosV2Query: FindAllTodosV2Query): List<TodoV2Repository.TodoV2Deadline> {
        return todoV2Repository.findAll()
    }
}