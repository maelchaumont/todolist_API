package com.example.todolist.restControllers

import TodoCSVExporter
import com.cikaba.ftp.CSVReader
import com.example.todolist.coreapi.queryMessage.*
import com.example.todolist.coreapi.todo.TodoDTO
import com.example.todolist.restControllers.dto.Cell
import com.example.todolist.restControllers.dto.Data
import com.example.todolist.restControllers.dto.Line
import com.example.todolist.restControllers.export.TodoExcelExporter
import com.example.todolist.saga.SagaTodoV2Deadline
import com.example.todolist.saga.messagesPart.FindAllSagaQuery
import com.example.todolist.saga.messagesPart.FindAllTodosV2Query
import com.example.todolist.saga.queryPart.TodoV2Repository
import mu.KotlinLogging
import org.axonframework.extensions.kotlin.query
import org.axonframework.extensions.kotlin.queryMany
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Paths
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

    @GetMapping("/todos/export/csv")
    fun exportToCSV(response: HttpServletResponse) {
        response.contentType = "application/octet-stream"
        val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val currentDateTime = dateFormatter.format(Date())
        val headerKey = "Content-Disposition"
        val headerValue = "attachment; filename=todos_$currentDateTime.csv"
        response.setHeader(headerKey, headerValue)
        val listTodo: CompletableFuture<List<TodoDTO>> = queryGateway.queryMany(FindAllTodoQuery())
        val outputStream = TodoCSVExporter().generate(
            data = listTodo.get()
                .map {
                    TodoCSVExporter.Todo(
                        it.id,
                        it.name,
                        it.description,
                        it.priority
                    )
                },
            out = response.outputStream,
        )
        outputStream.close()
    }

    data class Trainee(val age: Int, val informations: Map<String, String>)
    @GetMapping("/todos/export/csvLikeInFlow")
    fun exportToCSVLikeInFlow(response: HttpServletResponse) {
        response.contentType = "application/octet-stream"
        val dateFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val currentDateTime = dateFormatter.format(Date())
        val headerKey = "Content-Disposition"
        val headerValue = "attachment; filename=exportLikeFlow_$currentDateTime.csv"
        response.setHeader(headerKey, headerValue)

        val trainees = listOf(
            Trainee(47 ,mapOf("name" to "John", "othername" to "Doe", "untruc" to "tructruc", "chose" to "machin")),
            Trainee(27 ,mapOf("name" to "Yeye", "othername" to "Doe", "chose" to "machin")),
            Trainee(41 ,mapOf("name" to "SFQFDDQ", "chose" to "machin")),
            Trainee(12 ,mapOf("name" to "John", "othername" to "Doe", "untruc" to "tructruc", "chose" to "machin")),
            Trainee(47 ,mapOf("name" to "John", "untruc" to "tructr'uc", "chose" to "machin")),
            Trainee(47 ,mapOf("name" to "Jo'hn", "othername" to "Doe", "untruc" to "tructruc")),
        )

        val informationColumnCount = trainees.maxOf { trainee -> trainee.informations.size }
        val listAllTraineeInformations = trainees.find { trainee -> trainee.informations.size == informationColumnCount }?.informations?.keys?.toList()

        val headerLine = Line(
            listOf(
                *(1..informationColumnCount).map { index -> Cell("Champ $index") }.toTypedArray(),
                Cell("Age"),
            )
        )

        val traineesData =
            Data(
                lines = listOf(headerLine) + trainees.map { trainee ->
                    val informationListWithPadding = mutableListOf<Cell>()
                    var loopIterator = 0
                    trainee.informations.forEach { (key, value) ->
                        while (key != listAllTraineeInformations!![loopIterator]) {
                            informationListWithPadding.add(Cell(String()))
                            loopIterator++
                        }
                        informationListWithPadding.add(Cell(value))
                        loopIterator++
                    }

                    val blankCellList = mutableListOf<Cell>()
                    (1..(informationColumnCount - informationListWithPadding.size)).forEach {
                        blankCellList.add(Cell(String()))
                    }

                    Line(listOf(
                            informationListWithPadding,
                            blankCellList,
                            listOf(
                                Cell(trainee.age.toString()),
                            )
                        ).flatten()
                    )
                }
            )

        val outputStream = TodoCSVExporter().generateLinkInFlow(
            data = traineesData,
            out = response.outputStream,
        )
        outputStream.close()
    }

    @GetMapping("/todos/export/read-csv")
    fun readCSV(response: HttpServletResponse) {
        val path = Paths.get("").toAbsolutePath().parent.parent.parent.parent.toString()
        KotlinLogging.logger {  }.info { path+"""/machaumont2/cikaba/workspace/todolist/src/main/resources/exportLikeFlow_2022-07-07_10_45_05.csv""" }
        val listTrainees = CSVReader().readCSV(File(path+"""/machaumont2/cikaba/workspace/todolist/src/main/resources/exportLikeFlow_2022-07-07_10_45_05.csv"""))
        listTrainees.forEach { trainee ->
            KotlinLogging.logger {  }.info { "ça marche !! Champ 1 = ${trainee.champ1}" }
        }
    }
}