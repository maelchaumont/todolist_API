package com.example.todolist.command

import com.example.todolist.coreapi.CreateRealTodoCommand
import com.example.todolist.coreapi.TodoCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Aggregate
@Document("todo")
data class Todo @CommandHandler constructor(val createRealTodoCommand: CreateRealTodoCommand) {
    @Id
    val id: Int = createRealTodoCommand.id
    @Field(name = "name")
    val name: String = createRealTodoCommand.name
    @Field(name = "description")
    val description: String = createRealTodoCommand.description
    @Field(name = "priority")
    val priority: String = createRealTodoCommand.priority

    //Used in all constructors
    init {
        TodoCreatedEvent(this);
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