package com.example.todolist.query

import com.example.todolist.command.Todo
import com.example.todolist.coreapi.*
import com.example.todolist.query.FindOneTodoQuery
import com.google.common.collect.Iterables.find
import com.mongodb.client.MongoClientFactory
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class TodoProjection(@Autowired val todoRepository: TodoRepository, val myCommandGateway: CommandGateway) {

    @EventHandler
    fun on(todoDTOCreatedEvent: TodoDTOCreatedEvent) {
        val newId: Int
        if(todoRepository.findAll().isNotEmpty())
            newId = todoRepository.findAll().lastIndex+1
        else
            newId = 0
        myCommandGateway.sendAndWait<CreateRealTodoCommand>(CreateRealTodoCommand(newId,
                                                                                  todoDTOCreatedEvent.theTodoDTO.name,
                                                                                  todoDTOCreatedEvent.theTodoDTO.description,
                                                                                  todoDTOCreatedEvent.theTodoDTO.priority))
    }

    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        todoRepository.save(todoCreatedEvent.theTodo)
        return ResponseEntity(todoCreatedEvent.theTodo, HttpStatus.CREATED)
    }


    @EventHandler
    fun on(todoDeletedEvent: TodoDeletedEvent): ResponseEntity<Any> {
        if (todoRepository.findById(todoDeletedEvent.idToDelete).isPresent) {
            todoRepository.deleteById(todoDeletedEvent.idToDelete)
            return ResponseEntity("todo n°${todoDeletedEvent.idToDelete} deleted", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @EventHandler
    fun on(todoUpdatedEvent: TodoUpdatedEvent): ResponseEntity<Any> {
        if (todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).isPresent) { //trouve l'ancienne virsion du Todo possédant le m^me id que le nouveau
            val todoToUpdate = todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).get()
            todoToUpdate.name = todoUpdatedEvent.todoUpdated.name
            todoToUpdate.description = todoUpdatedEvent.todoUpdated.description
            todoToUpdate.priority = todoUpdatedEvent.todoUpdated.priority
            todoRepository.save(todoToUpdate)
            return ResponseEntity("todo n°${todoUpdatedEvent.todoUpdated.id} updated", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery): ResponseEntity<List<Todo>> {
        return ResponseEntity(todoRepository.findAll(), HttpStatus.OK)
    }


    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): ResponseEntity<Todo> {
        if (todoRepository.findById(findOneTodoQuery.id).isPresent)
            return ResponseEntity(todoRepository.findById(findOneTodoQuery.id).get(), HttpStatus.OK)
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @QueryHandler
    fun handle(countTodosQuery: CountTodosQuery): Long {
        return todoRepository.count()
    }
}
