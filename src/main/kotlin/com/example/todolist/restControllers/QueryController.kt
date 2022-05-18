package com.example.todolist.restControllers

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.export.TodoExcelExporter
import com.example.todolist.saga.SagaTodoV2Deadline
import com.example.todolist.saga.messagesPart.FindAllSagaQuery
import com.example.todolist.saga.messagesPart.FindAllTodosV2Query
import com.example.todolist.saga.queryPart.TodoV2Repository
import org.axonframework.extensions.kotlin.queryMany
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
    fun todosGET(): CompletableFuture<List<Todo>> = queryGateway.queryMany(FindAllTodoQuery())

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
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): List<Todo> { // = QueryParam
        return queryGateway.query(FindTodosByPriorityQuery(prio), ResponseTypes.multipleInstancesOf(Todo::class.java)).get()
    }

    //============== SUBTASKS ==============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/subtask")
    fun getSubtask(): List<Subtask> {
        return queryGateway.query(FindAllSubtasksQuery(), ResponseTypes.multipleInstancesOf(Subtask::class.java)).get()
    }

    //============ Todos V2 + SAGA ============

    @GetMapping("/todosV2")
    fun getTodosV2(): List<TodoV2Repository.TodoV2Deadline> {
        return queryGateway.query(FindAllTodosV2Query(), ResponseTypes.multipleInstancesOf(TodoV2Repository.TodoV2Deadline::class.java)).get()
    }

    @GetMapping("/todosV2/sagas")
    fun getAllTodoV2Sagas(): List<SagaTodoV2Deadline> {
        return queryGateway.query(FindAllSagaQuery(), ResponseTypes.multipleInstancesOf(SagaTodoV2Deadline::class.java)).get()
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
        val excelExporter = TodoExcelExporter(listTodo.map {
            TodoExcelExporter.Todo(it.id!!,
                    it.name!!,
                    it.description!!,
                    it.priority!!)
        })
        excelExporter.doExportXLSX(response)
    }
}