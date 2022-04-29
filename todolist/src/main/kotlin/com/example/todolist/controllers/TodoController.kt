package com.example.todolist.controllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.command.TodoNoIdDTO
import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.todo.DeleteTodoCommand
import com.example.todolist.coreapi.todo.TodoDTOCreatedEvent
import com.example.todolist.coreapi.todo.UpdateTodoCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.AddSubtasksToTodosCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.DeleteSubtasksFromTodosCommand
import com.example.todolist.query.*
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
    fun todosDELETEOne(@PathVariable id: Int){
        myCommandGateway.send<DeleteTodoCommand>(DeleteTodoCommand(id))
    }

    @PostMapping("todos/update")
    fun updateTodo(@RequestBody myJson: String){
        // pas sûr du tout que ça marche, il y a un cast un peu bizarre à la fin
        val idTodo: Int = (GsonJsonParser().parseMap(myJson)["id"] as Double).toInt()
        myCommandGateway.send<UpdateTodoCommand>(UpdateTodoCommand(ObjectMapper().readValue(myJson, Map::class.java) as Map<String, Any>, idTodo))
    }




    @GetMapping("/todos/priority")
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): ResponseEntity<MutableList<Todo>> { // = QueryParam
        return ResponseEntity(queryGateway.query(FindTodosByPriority(prio), ResponseTypes.multipleInstancesOf(Todo::class.java)).get(), HttpStatus.OK)
    }

    //============== SUBTASKS ==============

    @PostMapping("/subtask")
    fun addSubtask(@RequestParam("name") name: String) {
        val newSubtaskID = queryGateway.query(FindNewSubtaskIDQuery(), ResponseTypes.instanceOf(String::class.java)).get()
        myCommandGateway.send<CreateSubtaskCommand>(CreateSubtaskCommand(newSubtaskID, name))
    }

    @DeleteMapping("/subtask")
    fun delSubtask(@RequestParam("subtaskID") subtaskID: String) {
        myCommandGateway.send<DeleteTodoCommand>(DeleteSubtaskCommand(subtaskID))
    }

    //============== TODOs & SUBTASKS INTERACTION ==============

    @PostMapping("/todos/subtasks")
    fun todosAddSubtasks(@RequestBody jsonBody: String) {
        val idTodos = GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<Double>
        val subtasksIDs = GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach{ idTodo -> myCommandGateway.send<AddSubtasksToTodosCommand>(AddSubtasksToTodosCommand(idTodo.toInt(), subtasksList)) }
    }

    @DeleteMapping("/todos/subtasks")
    fun todosDelSubtasks(@RequestBody jsonBody: String) {
        val idTodos = GsonJsonParser().parseMap(jsonBody)["idTodos"] as List<Double>
        val subtasksIDs = GsonJsonParser().parseMap(jsonBody)["subtasksIDs"] as List<String>
        val subtasksList = queryGateway.query(FindSubtasksByIDQuery(subtasksIDs), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
        idTodos.forEach{ idTodo -> myCommandGateway.send<DeleteSubtasksFromTodosCommand>(DeleteSubtasksFromTodosCommand(idTodo.toInt(), subtasksList)) }
    }


    /*
    @PostMapping("/todos/add-subtasks")
    fun todosAddSubtask(@RequestBody jsonBody: TaskAndSubtasksDTO): ResponseEntity<Any> {
        val mainTodo: Todo = todos.find { todo -> todo.id == jsonBody.mainTodoId}
            ?: return ResponseEntity("Resource with id = mainTodoId not found", HttpStatus.NOT_FOUND) // ?: appelé Elvis operator = if mainTodo == null

        for (todo in todos) {
            if (todo.id in jsonBody.subtasksIds && todo.id != mainTodo.id) mainTodo.subTasks.add(todo)
        }
        return ResponseEntity("Resource created", HttpStatus.CREATED)
    }

    @DeleteMapping("/todos/delete-subtasks")
    fun todosDeleteSubtask(@RequestBody jsonBody: TaskAndSubtasksDTO): ResponseEntity<Any> {
        val mainTodo: Todo = todos.find { todo -> todo.id == jsonBody.mainTodoId}
            ?: return ResponseEntity("Resource with id = mainTodoId not found", HttpStatus.NOT_FOUND) // = if mainTodo == null

        for (todo in mainTodo.subTasks) {
            if (todo.id in jsonBody.subtasksIds) mainTodo.subTasks.remove(todo)
        }
        return ResponseEntity("Resource deleted", HttpStatus.CREATED)
    }
     */
}