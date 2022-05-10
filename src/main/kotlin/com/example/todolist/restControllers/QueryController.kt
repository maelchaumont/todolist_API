package com.example.todolist.restControllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.coreapi.queryMessage.*
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import java.util.concurrent.CompletableFuture

class QueryController(val queryGateway: QueryGateway) {

    //============== TODOS ==============

    @GetMapping("/todos")
    fun todosGET(): ResponseEntity<MutableList<Todo>> {
        return ResponseEntity(queryGateway.query(FindAllTodoQuery(), ResponseTypes.multipleInstancesOf(Todo::class.java)).get(), HttpStatus.OK)
    }


    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: UUID): ResponseEntity<Todo> {
        return ResponseEntity(queryGateway.query(FindOneTodoQuery(id), ResponseTypes.instanceOf(Todo::class.java)).get(), HttpStatus.OK)
    }

    @GetMapping("/todos/count")
    fun countTodos(): CompletableFuture<Long>? {
        return queryGateway.query(CountTodosQuery(), ResponseTypes.instanceOf(Long::class.java))
    }

    @GetMapping("/todos/priority")
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): ResponseEntity<MutableList<Todo>> { // = QueryParam
        return ResponseEntity(queryGateway.query(FindTodosByPriorityQuery(prio), ResponseTypes.multipleInstancesOf(Todo::class.java)).get(), HttpStatus.OK)
    }

    //============== SUBTASKS ==============

    @GetMapping("/subtask")
    fun getSubtask(): ResponseEntity<MutableList<Subtask>> {
        return ResponseEntity(queryGateway.query(FindAllSubtasksQuery(), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get(), HttpStatus.OK)
    }

    //============ SAGA Todos V2 ============
}