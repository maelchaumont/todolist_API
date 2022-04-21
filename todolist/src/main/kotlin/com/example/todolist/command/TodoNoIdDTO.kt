package com.example.todolist.command

import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.CreateTodoDTOCommand
import com.mongodb.client.FindIterable
import org.axonframework.commandhandling.CommandHandler
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class TodoNoIdDTO @CommandHandler constructor(createTodoCommand: CreateTodoDTOCommand) {
    init {
        //----------------- Query dans Command ???? mais je vois pas moyen de faire autrement pour récupérer l'id max
        val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
        val database = client.getDatabase("test") //normal java driver usage
        val todos = database.getCollection<Todo>() //KMongo extension method
        //-----------------

        val todosFindIterable : FindIterable<Todo> = todos.find()
        CreateRealTodoCommand(todosFindIterable.maxOf { todo -> todo.id }+1, createTodoCommand.name, createTodoCommand.description, createTodoCommand.priority)
    }
}