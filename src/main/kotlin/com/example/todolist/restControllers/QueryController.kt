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
    fun todosGET(): MutableList<Todo> {
        return queryGateway.query(FindAllTodoQuery(), ResponseTypes.multipleInstancesOf(Todo::class.java)).get()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: UUID): Todo {
        return queryGateway.query(FindOneTodoQuery(id), ResponseTypes.instanceOf(Todo::class.java)).get()
    }

    @GetMapping("/todos/count")
    fun countTodos(): CompletableFuture<Long> {
        return queryGateway.query(CountTodosQuery(), ResponseTypes.instanceOf(Long::class.java))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos/priority")
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): MutableList<Todo> { // = QueryParam
        return queryGateway.query(FindTodosByPriorityQuery(prio), ResponseTypes.multipleInstancesOf(Todo::class.java)).get()
    }

    //============== SUBTASKS ==============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/subtask")
    fun getSubtask(): MutableList<Subtask> {
        return queryGateway.query(FindAllSubtasksQuery(), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
    }

    //============ Todos V2 + SAGA ============

    @GetMapping("/todosV2")
    fun gatTodosV2(): MutableList<TodoV2Repository.TodoV2Deadline> {
        return queryGateway.query(FindAllTodosV2Query(), ResponseTypes.multipleInstancesOf(TodoV2Repository.TodoV2Deadline::class.java)).get()
    }

    /*
    //Actually the percentage is in the saga so idk if we can send a query to the saga
    @GetMapping("/todosV2/percentage-done")
    fun getPercentage(@RequestBody myJson: String) {
        val idTodoV2: UUID = UUID.fromString(GsonJsonParser().parseMap(myJson)["idTodoV2"] as String)
        return queryGateway.query()
    }
    */

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