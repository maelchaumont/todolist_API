package com.example.todolist.query

import com.example.todolist.command.Todo
import com.example.todolist.coreapi.todo.*
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksAddedToTodoEvent
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksDeletedFromTodoEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class TodoProjection(@Autowired val todoRepository: TodoRepository,
                     @Autowired val subtaskRepository: SubtaskRepository,
                     val myCommandGateway: CommandGateway) {

    @EventHandler
    fun on(todoDTOCreatedEvent: TodoDTOCreatedEvent) {
        val newId: Int
        if(todoRepository.findAll().isNotEmpty())
            newId = todoRepository.findAll().maxOf { todo -> todo.id as Int} + 1
        else
            newId = 0
        myCommandGateway.sendAndWait<CreateRealTodoCommand>(
            CreateRealTodoCommand(newId,
                                  todoDTOCreatedEvent.theTodoDTO.name,
                                  todoDTOCreatedEvent.theTodoDTO.description,
                                  todoDTOCreatedEvent.theTodoDTO.priority,
                                  todoDTOCreatedEvent.theTodoDTO.subtasks)
        )
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
        if (todoRepository.findById(todoUpdatedEvent.todoUpdated.id!!).isPresent) { //trouve l'ancienne version du Todo possédant le même id que le nouveau
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

    //=========== TODOS AND SUBTASKS INTERACTION ===========

    @EventHandler
    fun handle(subtasksAddedToTodoEvent: SubtasksAddedToTodoEvent)/*: ResponseEntity<Todo>*/ {
        todoRepository.save(subtasksAddedToTodoEvent.todo)
        //return ResponseEntity(subtasksAddedToTodoEvent.todo, HttpStatus.CREATED)
    }

    @EventHandler
    fun handle(subtasksDeletedFromTodoEvent: SubtasksDeletedFromTodoEvent) {
        todoRepository.save(subtasksDeletedFromTodoEvent.todo)
    }

    //=========== QUERY ===========

    @QueryHandler
    fun handle(findAllTodoQuery: FindAllTodoQuery): List<Todo> {
        return todoRepository.findAll()
    }


    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): Todo {
        return todoRepository.findById(findOneTodoQuery.id).get()
    }

    @QueryHandler
    fun handle(countTodosQuery: CountTodosQuery): Long {
        return todoRepository.count()
    }

    @QueryHandler
    fun handle(findTodosByPriority: FindTodosByPriority): List<Todo> {
        val listPrioritiesInDB : MutableList<String> = mutableListOf()
        for(todo in todoRepository.findAll()) {
            if(!listPrioritiesInDB.contains(todo.priority) && !todo.priority.equals(""))
                listPrioritiesInDB.add(todo.priority!!)
        }
        if(!listPrioritiesInDB.contains(findTodosByPriority.prioritySearched))
            throw java.lang.IllegalArgumentException("priority ${findTodosByPriority.prioritySearched} does not exist in database !")
        return todoRepository.findAll().filter { todo -> todo.priority.equals(findTodosByPriority.prioritySearched) }
    }
}
