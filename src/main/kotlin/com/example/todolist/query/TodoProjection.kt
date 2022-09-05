package com.example.todolist.query

import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import com.example.todolist.coreapi.todo.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class TodoProjection(@Autowired val todoRepository: TodoRepository,
                     @Autowired val mongoTemplate: MongoTemplate) {

    @EventHandler
    fun on(todoCreatedEvent: TodoCreatedEvent): ResponseEntity<Any> {
        val theTodoView = TodoRepository.TodoView(todoCreatedEvent.id,
                                                    todoCreatedEvent.name,
                                                    todoCreatedEvent.description,
                                                    todoCreatedEvent.priority,
                                                    todoCreatedEvent.subtasks.map {
                                                        TodoRepository.SubtaskView(it.id, it.name)
                                                    }.toMutableList())
        todoRepository.save(theTodoView)
        return ResponseEntity(theTodoView,HttpStatus.CREATED)
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
    fun handle(findAllTodoQuery: FindAllTodoQuery): List<TodoDTO> =
            todoRepository.findAll().map { todoView ->
                TodoDTO(
                        id = todoView.id,
                        name = todoView.name,
                        description = todoView.description,
                        priority = todoView.priority,
                        subtasks = todoView.subtasks.map { subtaskView ->
                            TodoDTO.Subtask(id = subtaskView.subtaskID, name = subtaskView.name)
                        },
                )
            }

    @QueryHandler
    fun handle(findAllTodosIDsQuery: FindAllTodosIDsQuery): List<UUID> = todoRepository.findAll().map { it.id }

    @QueryHandler
    fun handle(findOneTodoQuery: FindOneTodoQuery): Optional<TodoDTO> = todoRepository.findById(findOneTodoQuery.id).map {
        TodoDTO(it.id,
                it.name,
                it.description,
                it.priority,
                it.subtasks.map { TodoDTO.Subtask(it.subtaskID, it.name) })
    }

    @QueryHandler
    fun handle(countTodosQuery: CountTodosQuery): Long = todoRepository.count()

    @QueryHandler
    fun handle(findTodosByPriorityQuery: FindTodosByPriorityQuery): List<TodoDTO> {
        val listPrioritiesInDB : MutableList<String> = mutableListOf()
        todoRepository.findAll().map {
            if(!listPrioritiesInDB.contains(it.priority) && !it.priority.equals(""))
                listPrioritiesInDB.add(it.priority)
        }
        if(!listPrioritiesInDB.contains(findTodosByPriorityQuery.prioritySearched))
            throw java.lang.IllegalArgumentException("priority ${findTodosByPriorityQuery.prioritySearched} does not exist in database !")
        return todoRepository.findAll().filter { todo -> todo.priority.equals(findTodosByPriorityQuery.prioritySearched) }.map {
                     TodoDTO(it.id,
                            it.name,
                            it.description,
                            it.priority,
                            it.subtasks.map { TodoDTO.Subtask(it.subtaskID, it.name) })
                 }
    }



    //SUBTASKS in TODOS
    @EventHandler
    fun on(subtaskCreatedEvent: SubtaskCreatedEvent) {
        val myTodoView = mongoTemplate.findById(subtaskCreatedEvent.idTodoAttached, TodoRepository.TodoView::class.java, "todolist")
        checkNotNull(myTodoView)
        myTodoView.subtasks.add(TodoRepository.SubtaskView(subtaskCreatedEvent.idSubtask, subtaskCreatedEvent.name))
        mongoTemplate.save(myTodoView, "todolist")
    }

    @EventHandler
    fun on(subtaskDeletedEvent: SubtaskDeletedEvent) {
        val myTodoView = mongoTemplate.findById(subtaskDeletedEvent.todoAttachedID, TodoRepository.TodoView::class.java, "todolist")
        checkNotNull(myTodoView)
        myTodoView.subtasks.removeIf { subtask -> subtask.subtaskID.equals(subtaskDeletedEvent.subtaskDeletedID) }
        mongoTemplate.save(myTodoView, "todolist")
    }
}