package com.example.todolist.command

import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.CreateTodoDTOCommand
import com.mongodb.client.FindIterable
import org.axonframework.commandhandling.CommandHandler
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class TodoNoIdDTO @CommandHandler constructor(createTodoCommand: CreateTodoDTOCommand) {
    init {
        CreateRealTodoCommand(/*todosFindIterable.maxOf { todo -> todo.id }+*/1, createTodoCommand.name, createTodoCommand.description, createTodoCommand.priority)
    }
}