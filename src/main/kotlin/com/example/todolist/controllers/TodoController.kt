package com.example.todolist.controllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.command.TodoNoIdDTO
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.todo.CreateRealTodoCommand
import com.example.todolist.coreapi.todo.DeleteTodoCommand
import com.example.todolist.coreapi.todo.UpdateTodoCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.DeleteSubtasksFromTodosCommand
import com.fasterxml.jackson.databind.ObjectMapper
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.boot.json.GsonJsonParser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.CompletableFuture


@RestController
class TodoController(val myCommandGateway: CommandGateway, val queryGateway: QueryGateway, val myEventGateway: EventGateway) {


    //============== TODOS ==============

    @GetMapping("/todos")
    fun todosGET(): ResponseEntity<MutableList<Todo>> {
        return ResponseEntity(queryGateway.query(FindAllTodoQuery(), ResponseTypes.multipleInstancesOf(Todo::class.java)).get(), HttpStatus.OK)
    }


    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: UUID): ResponseEntity<Todo> {
        return ResponseEntity(queryGateway.query(FindOneTodoQuery(id), ResponseTypes.instanceOf(Todo::class.java)).get(), HttpStatus.OK)
    }



    @PostMapping("/todos")
    fun postController(@RequestBody todoNoIdDTO: TodoNoIdDTO) {
        myCommandGateway.send<CreateRealTodoCommand>(CreateRealTodoCommand(todoNoIdDTO.name, todoNoIdDTO.description, todoNoIdDTO.priority, todoNoIdDTO.subtasks))
    }

    @GetMapping("/todos/count")
    fun countTodos(): CompletableFuture<Long>? {
        return queryGateway.query(CountTodosQuery(), ResponseTypes.instanceOf(Long::class.java))
    }

    @DeleteMapping("/todos/{id}")
    fun todosDELETEOne(@PathVariable id: UUID): ResponseEntity<String>{
        myCommandGateway.send<DeleteTodoCommand>(DeleteTodoCommand(id))
        return ResponseEntity("Todo successfully deleted", HttpStatus.OK)
    }


    @PatchMapping("todos/update")
    fun updateTodo(@RequestBody myJson: String){
        val idTodo: UUID = UUID.fromString(GsonJsonParser().parseMap(myJson)["id"] as String)
        myCommandGateway.send<UpdateTodoCommand>(UpdateTodoCommand(ObjectMapper().readValue(myJson, Map::class.java) as Map<String, Any>, idTodo))
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

    @PostMapping("/subtask")
    fun addSubtask(@RequestBody myJson: String) {
        val subtaskName = GsonJsonParser().parseMap(myJson)["name"] as String
        val todoAttachedID = UUID.fromString(GsonJsonParser().parseMap(myJson)["todoAttachedID"] as String)
        myCommandGateway.send<CreateSubtaskCommand>(CreateSubtaskCommand(subtaskName, todoAttachedID))
    }

    @DeleteMapping("/subtask")
    fun deleteSubtask(@RequestBody myJson: String) {
        val subtaskID = UUID.fromString(GsonJsonParser().parseMap(myJson)["subtaskID"] as String)
        val todoAttachedID = UUID.fromString(GsonJsonParser().parseMap(myJson)["todoAttachedID"] as String)
        myCommandGateway.send<DeleteSubtaskCommand>(DeleteSubtaskCommand(subtaskID, todoAttachedID))
    }

    /*
    @PostMapping("/subtask")
    fun addSubtask(@RequestParam("name") name: String): ResponseEntity<Any> {
        myCommandGateway.send<CreateSubtaskCommand>(CreateSubtaskCommand(name))
        return ResponseEntity(HttpStatus.CREATED)
    }

    @DeleteMapping("/subtask/{subtaskID}")
    fun delSubtask(@PathVariable subtaskID: UUID): ResponseEntity<String>{
        myCommandGateway.send<DeleteTodoCommand>(DeleteSubtaskCommand(subtaskID))
        return ResponseEntity("Subtask successfully deleted", HttpStatus.OK)
    }
    */
    //============== TODOs & SUBTASKS INTERACTION ==============

    /*
    @PostMapping("/todos/subtasks")
    fun todosAddSubtasks(@RequestBody jsonBody: String) {
        val idTodos: MutableList<UUID> = mutableListOf()
        (GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<String>).forEach {
                idTodoString -> idTodos.add(UUID.fromString(idTodoString))
        }
        val subtasksIDs: MutableList<UUID> = mutableListOf()
        (GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>).forEach {
                idSubtaskString -> subtasksIDs.add(UUID.fromString(idSubtaskString))
        }
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach{ idTodo -> myCommandGateway.send<AddSubtaskToTodoCommand>(AddSubtaskToTodoCommand(idTodo, subtasksList)) }
    }
    */

    @DeleteMapping("/todos/subtasks")
    fun todosDelSubtasks(@RequestBody jsonBody: String) {
        val idTodos: MutableList<UUID> = mutableListOf()
        (GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<String>).forEach {
            idTodoString -> idTodos.add(UUID.fromString(idTodoString))
        }
        val subtasksIDs: MutableList<UUID> = mutableListOf()
        (GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>).forEach {
            idSubtaskString -> subtasksIDs.add(UUID.fromString(idSubtaskString))
        }
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach{ idTodo -> myCommandGateway.send<DeleteSubtasksFromTodosCommand>(DeleteSubtasksFromTodosCommand(idTodo, subtasksList)) }
    }
}