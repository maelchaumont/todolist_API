package com.example.todolist.controllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.command.TodoNoIdDTO
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.todo.DeleteTodoCommand
import com.example.todolist.coreapi.todo.TodoDTOCreatedEvent
import com.example.todolist.coreapi.todo.UpdateTodoCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.AddSubtasksToTodosCommand
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
import java.util.concurrent.CompletableFuture


//val dans le constructeur équivalent à (voir ci-dessous) dans la classe
// maVar: MaVar
// init {
//  this.maVar = maVar
// }

//RestController implémente Controller qui implémente lui-même Component. Component permet de retrouver un Bean, ici la CommandGateway passée en constructeur
@RestController
class TodoController(val myCommandGateway: CommandGateway, val queryGateway: QueryGateway, val myEventGateway: EventGateway) {


    /*
    var todos: MutableList<Todo> = mutableListOf(Todo(1, "PremierTodo","Desc",false, "high"),
        Todo(2, "TodoDeux","2scription",true, "low"),
        Todo(3, "Number3","Bonjour",false, "medium"))
     */

    //============== TODOS ==============

    @GetMapping("/todos")
    fun todosGET(): ResponseEntity<MutableList<Todo>> {
        return ResponseEntity(queryGateway.query(FindAllTodoQuery(), ResponseTypes.multipleInstancesOf(Todo::class.java)).get(), HttpStatus.OK)
    }


    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: Int): ResponseEntity<Todo> {
        return ResponseEntity(queryGateway.query(FindOneTodoQuery(id), ResponseTypes.instanceOf(Todo::class.java)).get(), HttpStatus.OK)
    }



    @PostMapping("/todos")
    fun postController(@RequestBody todoNoIdDTO: TodoNoIdDTO) {
        myEventGateway.publish(TodoDTOCreatedEvent(TodoNoIdDTO(todoNoIdDTO.name, todoNoIdDTO.description, todoNoIdDTO.priority, todoNoIdDTO.subtasks)))
    }

    @GetMapping("/todos/count")
    fun countTodos(): CompletableFuture<Long>? {
        return queryGateway.query(CountTodosQuery(), ResponseTypes.instanceOf(Long::class.java))
    }

    @DeleteMapping("/todos/{id}")
    fun todosDELETEOne(@PathVariable id: Int): ResponseEntity<String>{
        myCommandGateway.send<DeleteTodoCommand>(DeleteTodoCommand(id))
        return ResponseEntity("Todo successfully deleted", HttpStatus.OK)
    }

    @PatchMapping("todos/update")
    fun updateTodo(@RequestBody myJson: String){
        // pas sûr du tout que ça marche, il y a un cast un peu bizarre à la fin
        //en fait ça marche
        val idTodo: Int = (GsonJsonParser().parseMap(myJson)["id"] as Double).toInt()
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
    fun addSubtask(@RequestParam("name") name: String): ResponseEntity<Any> {
        var newSubtaskID: String? = queryGateway.query(FindNewSubtaskIDQuery(), ResponseTypes.instanceOf(String::class.java)).get()
        if(newSubtaskID.isNullOrEmpty())
            newSubtaskID = "sub0"
        myCommandGateway.send<CreateSubtaskCommand>(CreateSubtaskCommand(newSubtaskID, name))
        return ResponseEntity(HttpStatus.CREATED)
    }

    @DeleteMapping("/subtask/{subtaskID}")
    fun delSubtask(@PathVariable subtaskID: String): ResponseEntity<String>{
        myCommandGateway.send<DeleteTodoCommand>(DeleteSubtaskCommand(subtaskID))
        return ResponseEntity("Subtask successfully deleted", HttpStatus.OK)
    }

    //============== TODOs & SUBTASKS INTERACTION ==============

    @PostMapping("/todos/subtasks")
    fun todosAddSubtasks(@RequestBody jsonBody: String) {
        val idTodos = GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<Double>
        val subtasksIDs = GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach {
            todoID -> if(!queryGateway.query(FindAllTodosIDsQuery(), ResponseTypes.multipleInstancesOf(Int::class.java)).get().contains(todoID.toInt()))
                            throw IllegalArgumentException("Une des todos indiqués n'existe pas !")
        }
        subtasksIDs.forEach {
            subID ->  if(!queryGateway.query(FindAllSubtasksIDsQuery(), ResponseTypes.multipleInstancesOf(String::class.java)).get().contains(subID))
                            throw IllegalArgumentException("Une des subtasks indiquées n'existe pas !")
        }
        idTodos.forEach{ idTodo -> myCommandGateway.send<AddSubtasksToTodosCommand>(AddSubtasksToTodosCommand(idTodo.toInt(), subtasksList)) }
    }

    @DeleteMapping("/todos/subtasks")
    fun todosDelSubtasks(@RequestBody jsonBody: String) {
        val idTodos = GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<Double>
        val subtasksIDs = GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>
        idTodos.forEach {
                todoID -> if(!queryGateway.query(FindAllTodosIDsQuery(), ResponseTypes.multipleInstancesOf(Int::class.java)).get().contains(todoID.toInt()))
            throw IllegalArgumentException("Une des todos indiqués n'existe pas !")
        }
        subtasksIDs.forEach {
                subID ->  if(!queryGateway.query(FindAllSubtasksIDsQuery(), ResponseTypes.multipleInstancesOf(String::class.java)).get().contains(subID))
            throw IllegalArgumentException("Une des subtasks indiquées n'existe pas !")
        }
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach{ idTodo -> myCommandGateway.send<DeleteSubtasksFromTodosCommand>(DeleteSubtasksFromTodosCommand(idTodo.toInt(), subtasksList)) }
    }
}