package com.example.todolist.query

import com.example.todolist.command.Todo
import com.example.todolist.converter.TodoAndTodoViewConverter
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.todo.*
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtaskAddedToTodoEvent
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksDeletedFromTodoEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TodoProjection(@Autowired val todoRepository: TodoRepository,
                     val myCommandGateway: CommandGateway) {

    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(todoCreatedEvent.theTodo))
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
        if (todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).isPresent) { //find the older Todo version which has the same ID as the new one
            val todoToUpdate = todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).get()
            todoToUpdate.name = todoUpdatedEvent.todoUpdated.name.toString()
            todoToUpdate.description = todoUpdatedEvent.todoUpdated.description.toString()
            todoToUpdate.priority = todoUpdatedEvent.todoUpdated.priority.toString()
            todoRepository.save(todoToUpdate)
            return ResponseEntity("todo n°${todoUpdatedEvent.todoUpdated.id} updated", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
        //todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).get().
    }

    //=========== TODOS AND SUBTASKS INTERACTION ===========

    @EventHandler
    fun on(subtasksAddedToTodoEvent: SubtaskAddedToTodoEvent) {
        todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(subtasksAddedToTodoEvent.todo))
    }

    @EventHandler
    fun handle(subtasksAddedToTodoEvent: SubtaskAddedToTodoEvent) {
        todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(subtasksAddedToTodoEvent.todo))
    }

    @EventHandler
    fun handle(subtasksDeletedFromTodoEvent: SubtasksDeletedFromTodoEvent) {
        todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(subtasksDeletedFromTodoEvent.todo))
    }

    //=========== QUERY ===========

    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery): List<Todo> {
        val listToReturn: MutableList<Todo> = mutableListOf()
        for (todoView in todoRepository.findAll()) {
            listToReturn.add(TodoAndTodoViewConverter().convertTodoViewToTodo(todoView))
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findAllTodosIDsQuery: FindAllTodosIDsQuery): List<UUID> {
        val listToReturn: MutableList<UUID> = mutableListOf()
        for (todoView in todoRepository.findAll()) {
            listToReturn.add(TodoAndTodoViewConverter().convertTodoViewToTodo(todoView).id!!)
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): Todo {
        return TodoAndTodoViewConverter().convertTodoViewToTodo(todoRepository.findById(findOneTodoQuery.id).get())
    }

    @QueryHandler
    fun handle(countTodosQuery: CountTodosQuery): Long {
        return todoRepository.count()
    }

    @QueryHandler
    fun handle(findTodosByPriorityQuery: FindTodosByPriorityQuery): List<Todo> {
        val listPrioritiesInDB : MutableList<String> = mutableListOf()
        for(todo in todoRepository.findAll()) {
            if(!listPrioritiesInDB.contains(todo.priority) && !todo.priority.equals(""))
                listPrioritiesInDB.add(todo.priority)
        }
        if(!listPrioritiesInDB.contains(findTodosByPriorityQuery.prioritySearched))
            throw java.lang.IllegalArgumentException("priority ${findTodosByPriorityQuery.prioritySearched} does not exist in database !")
        val listTodo: MutableList<Todo> = mutableListOf()
        for(todoView in todoRepository.findAll().filter { todo -> todo.priority.equals(findTodosByPriorityQuery.prioritySearched) })
            listTodo.add(TodoAndTodoViewConverter().convertTodoViewToTodo(todoView))
        return listTodo
    }
}
