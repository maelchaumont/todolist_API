package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.converter.TodoAndTodoViewConverter
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.todo.*
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
        //todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(todoCreatedEvent.theTodo))
        val theTodo = Todo(todoCreatedEvent.id,
                            todoCreatedEvent.name,
                            todoCreatedEvent.description,
                            todoCreatedEvent.priority,
                            todoCreatedEvent.subtasks.map { Subtask(it.id, it.name) }.toMutableList())
        val theTodoView = TodoAndTodoViewConverter().convertTodoToTodoView(theTodo)
        todoRepository.save(theTodoView)
        return ResponseEntity(theTodoView, HttpStatus.CREATED)
    }


    @EventHandler
    fun on(todoDeletedEvent: TodoDeletedEvent): ResponseEntity<Any> {
        if (todoRepository.findById(todoDeletedEvent.id).isPresent) {
            todoRepository.deleteById(todoDeletedEvent.id)
            return ResponseEntity("todo n°${todoDeletedEvent.id} deleted", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @EventHandler
    fun on(todoInfoUpdatedEvent: TodoInfoUpdatedEvent): ResponseEntity<Any> {
        if (todoRepository.findById(todoInfoUpdatedEvent.id).isPresent) { //find the older Todo version which has the same ID as the new one
            val todoToUpdate = todoRepository.findById(todoInfoUpdatedEvent.id).get()
            todoToUpdate.name = todoInfoUpdatedEvent.name.toString()
            todoToUpdate.description = todoInfoUpdatedEvent.description.toString()
            todoRepository.save(todoToUpdate)
            return ResponseEntity("todo n°${todoInfoUpdatedEvent.id}'s infos(name & description) updated", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @EventHandler
    fun on(todoPriorityUpdatedEvent: TodoPriorityUpdatedEvent): ResponseEntity<Any> {
        if (todoRepository.findById(todoPriorityUpdatedEvent.id).isPresent) {
            val todoToUpdate = todoRepository.findById(todoPriorityUpdatedEvent.id).get()
            todoToUpdate.priority = todoPriorityUpdatedEvent.priority
            todoRepository.save(todoToUpdate)
            return ResponseEntity("todo n°${todoPriorityUpdatedEvent.id}'s infos(name & description) updated", HttpStatus.OK)
        }
        else
            return ResponseEntity(HttpStatus.NOT_FOUND)
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
