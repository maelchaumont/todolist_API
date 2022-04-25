package com.example.todolist.controllers

import com.example.todolist.command.TodoNoIdDTO
import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.DeleteTodoCommand
import com.example.todolist.query.FindAllTodoQuery
import com.example.todolist.queryr.FindOneTodoQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*


//val dans le constructeur équivalent à (voir ci-dessous) dans la classe
// maVar: MaVar
// init {
//  this.maVar = maVar
// }

//RestController implémente Controller qui implémente lui-même Component. Component permet de retrouver un Bean, ici la CommandGateway passée en constructeur
@RestController
class TodoController(val myCommandGateway: CommandGateway) {


    /*
    var todos: MutableList<Todo> = mutableListOf(Todo(1, "PremierTodo","Desc",false, "high"),
        Todo(2, "TodoDeux","2scription",true, "low"),
        Todo(3, "Number3","Bonjour",false, "medium"))
     */

    @GetMapping("/todos")
    fun todosGET(): FindAllTodoQuery{
        return FindAllTodoQuery()
    }


    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: Int) {
        FindOneTodoQuery(id)
    }



    @PostMapping("/todos")
    fun postController(@RequestBody todoNoIdDTO: TodoNoIdDTO) {
        //return CreateRealTodoCommand(1, createTodoDTOCommand.name, createTodoDTOCommand.description, createTodoDTOCommand.priority)
        //val commandGateway: CommandGateway = DefaultCommandGateway.builder().build()
        //commandGateway.send<CreateRealTodoCommand>(CreateRealTodoCommand(1, createTodoDTOCommand.name, createTodoDTOCommand.description, createTodoDTOCommand.priority))
        //val myCommandBus: CommandBus = SimpleCommandBus.builder().build()
        //val commandGateway: CommandGateway = DefaultCommandGateway.builder().commandBus(myCommandBus).build()
        /*
        commandBus.dispatch(
            GenericCommandMessage.asCommandMessage(CreateRealTodoCommand(1, createTodoDTOCommand.name, createTodoDTOCommand.description, createTodoDTOCommand.priority)),
            FutureCallback<Todo, Void>()
        )*/
        myCommandGateway.sendAndWait<CreateRealTodoCommand>(CreateRealTodoCommand(5, todoNoIdDTO.name, todoNoIdDTO.description, todoNoIdDTO.priority))
    }


    @DeleteMapping("/todos/{id}")
    fun todosDELETEOne(@PathVariable id: Int){
        DeleteTodoCommand(id)
    }
    /*
    @DeleteMapping("/todos/{id}")
    fun todosDELETEOne(@PathVariable id: Int): ResponseEntity<Any> {
        for(todo in todos) {
            if (todo.id == id) {
                todos.remove(todo)
                return ResponseEntity("Resource deleted successfully", HttpStatus.CREATED)
            }
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
     */

    /*
    @GetMapping("/todos/not-finished")
    fun todosNotFinished(): ResponseEntity<List<Todo>> {
        return ResponseEntity(todos.filter {todo -> !todo.finished}, HttpStatus.OK)
    }

     */

    /*
    @GetMapping("/todos/priority")
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): ResponseEntity<Any> { // = QueryParam
        val LIST_PRIORITIES : List<String> = listOf("low", "medium", "high")
        if(prio in LIST_PRIORITIES)
            return ResponseEntity(todos.filter { todo -> todo.priority == prio }, HttpStatus.OK)
        else
            return ResponseEntity("Existing priorities are : $LIST_PRIORITIES", HttpStatus.NOT_FOUND)
    }

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