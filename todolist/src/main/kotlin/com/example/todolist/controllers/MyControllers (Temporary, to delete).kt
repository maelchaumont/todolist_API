package com.example.todolist.controllers

import com.example.todolist.command.TaskAndSubtasksDTO
import com.example.todolist.command.Todo
import com.example.todolist.command.TodoNoIdDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class MyControllers {
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
        fun randNumber(): String {
            return "Random number is " + generateRand(a,b).toString()
        }

        fun generateRand(from: Int, to: Int) : Int {
            return random.nextInt(to - from) + from
        }
    }

    @RestController
    class TodoController {
        var todos: MutableList<Todo> = mutableListOf(Todo(1, "PremierTodo","Desc",false, "high"),
            Todo(2, "TodoDeux","2scription",true, "low"),
            Todo(3, "Number3","Bonjour",false, "medium"))

        @GetMapping("/todos")
        fun todosGET(): MutableList<Todo> {
            return todos
        }

        @GetMapping("/todos/{id}")
        fun todosGETOne(@PathVariable id: Int): ResponseEntity<Todo> {
            for(todo in todos) {
                if (todo.id == id) return ResponseEntity<Todo>(todo, HttpStatus.OK) //j'aurais utilisé any{} mais je  n'arrive pas à récupérer le todo dans le prédicat
            }
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        @PostMapping("/todos")
        fun postController(@RequestBody todoTemp: TodoNoIdDTO): ResponseEntity<Any> {
            val todoWithMaxId : Todo? = todos.maxByOrNull {todo -> todo.id}
            if (todoWithMaxId != null)
                todos.add(Todo(todoWithMaxId.id+1, todoTemp.name, todoTemp.description, false, todoTemp.priority))
            else
                todos.add(Todo(1, todoTemp.name, todoTemp.description, false, todoTemp.priority))
            return ResponseEntity(HttpStatus.CREATED)
        }

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

        @GetMapping("/todos/not-finished")
        fun todosNotFinished(): ResponseEntity<List<Todo>> {
            return ResponseEntity(todos.filter {todo -> !todo.finished}, HttpStatus.OK)
        }

        @GetMapping("/todos/priority")
        fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): ResponseEntity<Any> { // = QueryParam
            val LIST_PRIORITIES : List<String> = listOf("low", "medium", "high")
            if(prio in LIST_PRIORITIES)
                return ResponseEntity(todos.filter { todo -> todo.priority == prio }, HttpStatus.OK)
            else
                return ResponseEntity("Existing priorities are : $LIST_PRIORITIES", HttpStatus.NOT_FOUND)

            /*
            val todosPrio = mutableListOf<Todo>()
            for (todo in todos) {
                if (todo.priority == prio) todosPrio.add(todo)
            }
            return todosPrio
             */
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
    }
}