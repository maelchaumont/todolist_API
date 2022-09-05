
import com.example.todolist.restControllers.dto.Data
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.*

class TodoCSVExporter {

    //Todo representation on the csv format
    data class Todo(val id: UUID,
                    val name: String,
                    val description: String,
                    val priority: String)

    fun generate(data: List<Todo>, out: OutputStream): OutputStream {
        val printer = CSVPrinter(
            OutputStreamWriter(out),
            CSVFormat.EXCEL.builder().setDelimiter(';').build()
        )

        data.forEach { todo ->
            printer.printRecord(todo.id, todo.name, todo.description, todo.priority)
        }

        printer.flush()
        printer.close()

        return out
    }

    fun generateLinkInFlow(data: Data, out: OutputStream): OutputStream {
        //CSV means comma separated value, but in french excel delimiter is ;
        val printer = CSVPrinter(
            OutputStreamWriter(out),
            CSVFormat.EXCEL.builder().setDelimiter(';').build()
        )

        data.lines.forEach { line ->
            printer.printRecord(line.cells.map { it.content })
        }

        printer.flush()
        printer.close()

        return out
    }
}