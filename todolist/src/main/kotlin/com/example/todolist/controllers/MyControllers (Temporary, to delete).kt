package com.example.todolist.controllers

import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.CreateTodoDTOCommand
import com.example.todolist.coreapi.DeleteTodoCommand
import com.example.todolist.query.FindAllTodoQuery
import com.example.todolist.queryr.FindOneTodoQuery
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.CommandGatewayFactory
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


class `MyControllers (Temporary, to delete)` {
    @RestController
    class HelloController {
        @GetMapping("/hello")
        fun hello(@RequestParam(value = "name", defaultValue = "World") name : String): String {
            return "Hello $name !"
        }
    }


    @RestController
    class RandController {
        //--------VARIABLES--------
        //read-only = val. others = var
        val a: Int = 1  // immediate assignment
        val b = 20   // `Int` type is inferred
        var random = Random();

        @GetMapping("/randNumber")
        fun randNumber(): ResponseEntity<Any> {
            return ResponseEntity.of(Optional.of(generateRand(a,b)))
        }

        fun generateRand(from: Int, to: Int) : Int {
            return random.nextInt(to - from) + from
        }
    }

    @RestController
    class TodoController {
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
        fun postController(@RequestBody createTodoDTOCommand: CreateTodoDTOCommand): CreateRealTodoCommand {
            return CreateRealTodoCommand(1, createTodoDTOCommand.name, createTodoDTOCommand.description, createTodoDTOCommand.priority)
            //val commandGateway: CommandGateway = DefaultCommandGateway.builder().build()
            //commandGateway.send<CreateRealTodoCommand>(CreateRealTodoCommand(1, createTodoDTOCommand.name, createTodoDTOCommand.description, createTodoDTOCommand.priority))
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
}