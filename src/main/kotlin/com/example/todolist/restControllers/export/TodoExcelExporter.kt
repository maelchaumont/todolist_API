package com.example.todolist.restControllers.export

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellUtil.createCell
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.*
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse


class TodoExcelExporter(val listTodo: List<Todo>) {
    val workbook: XSSFWorkbook = XSSFWorkbook()
    var sheet: XSSFSheet = workbook.createSheet("Todos")

    //Todo representation on the excel worksheet
    data class Todo(val id: UUID,
                    val name: String,
                    val description: String,
                    val priority: String)

    fun writeHeader() {
        val row: Row = sheet.createRow(0)

        val style: CellStyle = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.setFontHeight(16.0)
        style.setFont(font)

        createCell(row, 0, "id")
        createCell(row, 1, "name")
        createCell(row, 2, "desc")
        createCell(row, 3, "priority")
    }

    fun writeDataLines() {
        var rowCount = 1
        listTodo.map {
            val row: Row = sheet.createRow(rowCount)
            var columnCount = 0
            createCell(row, columnCount, it.id.toString())
            columnCount++
            createCell(row, columnCount, it.name)
            columnCount++
            createCell(row, columnCount, it.description)
            columnCount++
            createCell(row, columnCount, it.priority)
            rowCount++
        }
    }

    fun doExportXLSX(httpServletResponse: HttpServletResponse) {
        writeHeader()
        writeDataLines()

        val outputStream: ServletOutputStream = httpServletResponse.outputStream
        workbook.write(outputStream)
        workbook.close()
        outputStream.close()
    }
}