package com.example.todolist.restControllers

import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.todo.TodoDTO
import com.example.todolist.restControllers.export.TodoExcelExporter
import com.example.todolist.saga.SagaTodoV2Deadline
import com.example.todolist.saga.messagesPart.FindAllSagaQuery
import com.example.todolist.saga.messagesPart.FindAllTodosV2Query
import com.example.todolist.saga.queryPart.TodoV2Repository
import org.axonframework.extensions.kotlin.query
import org.axonframework.extensions.kotlin.queryMany
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletResponse

@RestController
class QueryController(@Autowired val queryGateway: QueryGateway) {

    //============== TODOS ==============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos")
    fun todosGET(): CompletableFuture<List<TodoDTO>> = queryGateway.queryMany(FindAllTodoQuery())

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos/{id}")
    fun todosGETOne(@PathVariable id: UUID): CompletableFuture<TodoDTO> = queryGateway.query(FindOneTodoQuery(id))

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos/count")
    fun countTodos(): CompletableFuture<Long> = queryGateway.query(CountTodosQuery())

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todos/priority")
    // RequestParam = QueryParam (?prio=...)
    fun todosByPriority(@RequestParam("prio", defaultValue = "medium") prio: String): CompletableFuture<List<TodoDTO>> = queryGateway.queryMany(FindTodosByPriorityQuery(prio))


    //============== SUBTASKS ==============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/subtask")
    fun getSubtask(): CompletableFuture<List<TodoDTO.Subtask>> = queryGateway.queryMany(FindAllSubtasksQuery())

    //============ Todos V2 + SAGA ============

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todosV2")
    fun getTodosV2(): CompletableFuture<List<TodoV2Repository.TodoV2Deadline>> = queryGateway.queryMany(FindAllTodosV2Query())

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/todosV2/sagas")
    fun getAllTodoV2Sagas(): CompletableFuture<List<SagaTodoV2Deadline>> = queryGateway.queryMany(FindAllSagaQuery())

    //============== EXPORT ==============

    @GetMapping("/todos/export/xlsx")
    fun exportToExcel(response: HttpServletResponse) {
        response.contentType = "application/octet-stream"
        val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val currentDateTime = dateFormatter.format(Date())
        val headerKey = "Content-Disposition"
        val headerValue = "attachment; filename=todos_$currentDateTime.xlsx"
        response.setHeader(headerKey, headerValue)
        val listTodo: CompletableFuture<List<TodoDTO>> = queryGateway.queryMany(FindAllTodoQuery())
        val excelExporter = TodoExcelExporter(listTodo.get().map {
            TodoExcelExporter.Todo(it.id,
                                   it.name,
                                   it.description,
                                   it.priority)
        })
        excelExporter.doExportXLSX(response)
    }
}