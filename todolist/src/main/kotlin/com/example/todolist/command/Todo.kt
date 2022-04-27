package com.example.todolist.command

import com.example.todolist.TodolistApplication
import com.example.todolist.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId

@Aggregate
@Document(collection = "todolist")
//no args Constructor //pas compatible avec mot clé data, jsp pourquoi
class Todo constructor()  {
    @MongoId
    @AggregateIdentifier
    var id: Int? = null //pas initialisé à la bonne valeur (pour le constructeur sans paramètres)
    @Field(name = "name")
    var name: String? = null
    @Field(name = "description")
    var description: String? = null
    @Field(name = "priority")
    var priority: String? = null

    @CommandHandler
    constructor(createRealTodoCommand: CreateRealTodoCommand) : this() {
        this.id = createRealTodoCommand.id
        this.name = createRealTodoCommand.name
        this.description = createRealTodoCommand.description
        this.priority = createRealTodoCommand.priority
        AggregateLifecycle.apply(TodoCreatedEvent(this))
    }

    @CommandHandler
    fun updateTodo(updateTodoCommand: UpdateTodoCommand) {
        for(entry in updateTodoCommand.mapThingsToUpdate.entries) {
            when(entry.key) { // = switch
                "name" -> this.name = entry.value as String
                "description" -> this.description = entry.value as String
                "priority" -> this.priority = entry.value as String
            }
        }
        apply(TodoUpdatedEvent(this))
    }

    @CommandHandler
    fun deleteTodo(deleteTodoCommand: DeleteTodoCommand){
        apply(TodoDeletedEvent(id as Int))
    }


    @EventSourcingHandler
    fun on(todoCreatedEvent: TodoCreatedEvent){
        this.id = todoCreatedEvent.theTodo.id
        this.name = todoCreatedEvent.theTodo.name
        this.description = todoCreatedEvent.theTodo.description
        this.priority = todoCreatedEvent.theTodo.priority
    }

    @EventSourcingHandler
    fun on(todoUpdatedEvent: TodoUpdatedEvent) {
        this.name = todoUpdatedEvent.todoUpdated.name
        this.description = todoUpdatedEvent.todoUpdated.description
        this.priority = todoUpdatedEvent.todoUpdated.priority
    }

    @EventSourcingHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        markDeleted()
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Todo

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (priority != other.priority) return false

        return true
    }

    override fun toString(): String {
        return "Todo(id=$id, name='$name', description='$description', priority='$priority')"
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        return result
    }
}