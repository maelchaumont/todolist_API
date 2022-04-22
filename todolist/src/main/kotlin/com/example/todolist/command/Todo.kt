package com.example.todolist.command

import com.example.todolist.TodolistApplication
import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.TodoCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Aggregate
@Document(collection = "todolist")
//no args Constructor //pas compatible avec mot clé data, jsp pourquoi
class Todo constructor()  {
    @Id
    @AggregateIdentifier
    var id: Int = 0 //pas initialisé à la bonne valeur (pour le constructeur sans paramètres)
    @Field(name = "name")
    var name: String = ""
    @Field(name = "description")
    var description: String = ""
    @Field(name = "priority")
    var priority: String = ""

    //Used in all constructors
    init {
        AggregateLifecycle.apply(TodoCreatedEvent(this))
    }

    @CommandHandler
    constructor(createRealTodoCommand: CreateRealTodoCommand) : this() {
        this.id = createRealTodoCommand.id
        this.name = createRealTodoCommand.name
        this.description = createRealTodoCommand.description
        this.priority = createRealTodoCommand.priority
    }
}


/*
@Aggregate
data class Todo(@AggregateIdentifier @Id val id: Int, val name: String, val description: String, val priority: String) {

    @AggregateMember
    var subTasks: MutableList<Todo> = mutableListOf()

    @CommandHandler
    constructor(id: Int, name: String, description: String, priority: String, subTasks: MutableList<Todo>) :this(id, name, description, priority) {
        this.subTasks = subTasks
    }

    //Used in all constructors
    init {
        TodoCreatedEvent(this);
    }
}
 */