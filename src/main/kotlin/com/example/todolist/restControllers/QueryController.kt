package com.example.todolist.restControllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.export.TodoExcelExporter
import com.example.todolist.saga.messagesPart.FindAllTodosV2Query
import com.example.todolist.saga.queryPart.TodoV2Repository
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletResponse

@RestController
class QueryController(val queryGateway: QueryGateway) {

    //============== TODOS ==============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos")
    fun todosGET(): ResponseEntity<MutableList<Todo>> { //CompletableFuture<MutableList<Todo>> avec pas de .get()
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

    @GetMapping("/todosV2")
    fun gatTodosV2(): MutableList<TodoV2Repository.TodoV2Deadline>? {
        return queryGateway.query(FindAllTodosV2Query(), ResponseTypes.multipleInstancesOf(TodoV2Repository.TodoV2Deadline::class.java)).get()
    }

    //============== EXPORT ==============

    @GetMapping("/todos/export/xlsx")
    fun exportToExcel(response: HttpServletResponse) {
        response.contentType = "application/octet-stream"
        val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val currentDateTime = dateFormatter.format(Date())
        val headerKey = "Content-Disposition"
        val headerValue = "attachment; filename=todos_$currentDateTime.xlsx"
        response.setHeader(headerKey, headerValue)
        val listTodo: List<Todo> = queryGateway.query(FindAllTodoQuery(), ResponseTypes.multipleInstancesOf(Todo::class.java)).get()
        val excelExporter = TodoExcelExporter(listTodo.map {TodoExcelExporter.Todo(it.id!!,
                                                                                    it.name!!,
                                                                                    it.description!!,
                                                                                    it.priority!!)})
        excelExporter.doExportXLSX(response)
    }
}