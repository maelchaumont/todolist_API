package com.cikaba.ftp

import com.example.todolist.restControllers.dto.Trainee
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class CSVReader{

    fun readCSV(fileCSV: File): List<Trainee> {
        val reader = BufferedReader(FileReader(fileCSV))
        val csvParser = CSVParser(
            reader,
            CSVFormat
                .Builder
                .create()
                .setDelimiter(';')
                .setHeader()
                .build(),
        )

        val listTrainees = csvParser.map { csvRecord ->
            Trainee(
                champ1 = csvRecord["Champ 1"],
                champ2 = csvRecord["Champ 2"],
                champ3 = csvRecord["Champ 3"],
                champ4 = csvRecord["Champ 4"],
                age = csvRecord["Age"],
            )
        }
        reader.close()
        return listTrainees
    }
}
