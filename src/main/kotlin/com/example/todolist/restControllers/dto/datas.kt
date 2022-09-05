package com.example.todolist.restControllers.dto

data class Data(val lines: List<Line>)
data class Line(val cells: List<Cell>)
data class Cell(val content: String)

data class Trainee(
    val champ1: String,
    val champ2: String,
    val champ3: String,
    val champ4: String,
    val age: String,
)