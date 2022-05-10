package com.example.todolist.restControllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.TodoNoIdDTO
import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.todo.CreateTodoCommand
import com.example.todolist.coreapi.todo.DeleteTodoCommand
import com.example.todolist.coreapi.todo.UpdateTodoInfoCommand
import com.example.todolist.saga.commandPart.CreateTodoV2Command
import com.example.todolist.saga.commandPart.DeleteTodoV2Command
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.boot.json.GsonJsonParser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class CommandController(val myCommandGateway: CommandGateway) {


    //============== TODOS ==============


    @PostMapping("/todos")
    fun postTodo(@RequestBody todoNoIdDTO: TodoNoIdDTO) {
        myCommandGateway.send<CreateTodoCommand>(CreateTodoCommand(todoNoIdDTO.name,
                                                                    todoNoIdDTO.description,
                                                                    todoNoIdDTO.priority,
                                                                    todoNoIdDTO.subtasks.map { CreateTodoCommand.Subtask(it.name!!) }))
    }


    @DeleteMapping("/todos/{id}")
    fun todosDELETEOne(@PathVariable id: UUID): ResponseEntity<String>{
        myCommandGateway.send<DeleteTodoCommand>(DeleteTodoCommand(id))
        return ResponseEntity("Todo successfully deleted", HttpStatus.OK)
    }


    @PatchMapping("todos/update")
    fun updateTodo(@RequestBody myJson: String){
        val idTodo: UUID = UUID.fromString(GsonJsonParser().parseMap(myJson)["id"] as String)
        val name: String? = GsonJsonParser().parseMap(myJson)["name"] as String?
        val description: String? = GsonJsonParser().parseMap(myJson)["description"] as String?
        val priority: String? = GsonJsonParser().parseMap(myJson)["priority"] as String?
        val subtasks: List<Subtask>? = GsonJsonParser().parseMap(myJson)["subtasks"] as List<Subtask>?

        if(!name.isNullOrBlank() or !description.isNullOrBlank())
            myCommandGateway.send<UpdateTodoInfoCommand>(UpdateTodoInfoCommand(idTodo, name, description))
        //myCommandGateway.send<UpdateTodoCommand>(UpdateTodoCommand(ObjectMapper().readValue(myJson, Map::class.java) as Map<String, Any>, idTodo))
    }


    //============== SUBTASKS ==============

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

    /*  WOULD NEED TO USE A SAGA
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
    */

    //============ SAGA Todos V2 ============

    @PostMapping("/todosV2")
    fun postTodoV2(@RequestBody todoNoIdDTO: TodoNoIdDTO) {
        //TOD0 can be modified for a random number of minutes after its creation (between 5 and 15)
        myCommandGateway.send<CreateTodoV2Command>(CreateTodoV2Command(todoNoIdDTO.name,
                                                    todoNoIdDTO.description,
                                                    todoNoIdDTO.priority,
                                                    todoNoIdDTO.subtasks.map { CreateTodoV2Command.Subtask(it.subtaskID!!, it.name!!) },
                                                    Random().nextInt(10)+5))
    }

    @DeleteMapping("/todosV2")
    fun delTodoV2(@RequestBody myJson: String) {
        val idTodoToDelete: UUID = UUID.fromString(GsonJsonParser().parseMap(myJson)["idTodoV2"] as String)
        myCommandGateway.send<DeleteTodoV2Command>(DeleteTodoV2Command(idTodoToDelete))
    }
}