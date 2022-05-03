package com.example.todolist.command

import com.example.todolist.coreapi.todo.*
import com.example.todolist.coreapi.todoAndSubtaskInteractions.AddSubtasksToTodosCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.DeleteSubtasksFromTodosCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksAddedToTodoEvent
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksDeletedFromTodoEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId

@Aggregate
//no args Constructor //pas compatible avec mot clé data, jsp pourquoi
class Todo constructor()  {
    @AggregateIdentifier
    var id: Int? = null //pas initialisé à la bonne valeur (pour le constructeur sans paramètres)
    var name: String? = null
    var description: String? = null
    var priority: String? = null

    @Field(name = "subtasks")
    var subtasks: MutableList<Subtask> = mutableListOf()

    @CommandHandler
    constructor(createRealTodoCommand: CreateRealTodoCommand) : this() {
        this.id = createRealTodoCommand.id
        this.name = createRealTodoCommand.name
        this.description = createRealTodoCommand.description
        this.priority = createRealTodoCommand.priority
        this.subtasks = createRealTodoCommand.subtasks
        AggregateLifecycle.apply(TodoCreatedEvent(this))
    }

    //appelé par le TodoAndTodoViewConverter
    constructor(id: Int, name: String, description: String, priority: String, subtasks: MutableList<Subtask>) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.priority = priority
        this.subtasks = subtasks
    }

    @CommandHandler
    fun updateTodo(updateTodoCommand: UpdateTodoCommand) {
        for(entry in updateTodoCommand.mapThingsToUpdate.entries) {
            when(entry.key) { // = switch
                "name" -> this.name = entry.value as String
                "description" -> this.description = entry.value as String
                "priority" -> this.priority = entry.value as String
                "subtasks" -> this.subtasks = entry.value as MutableList<Subtask>
            }
        }
        apply(TodoUpdatedEvent(this))
    }

    @CommandHandler
    fun deleteTodo(deleteTodoCommand: DeleteTodoCommand){
        apply(TodoDeletedEvent(id as Int))
    }

    @CommandHandler
    fun addSubtasks(addSubtasksToTodosCommand: AddSubtasksToTodosCommand) {
        subtasks.addAll(addSubtasksToTodosCommand.subtasksToAdd)
        apply(SubtasksAddedToTodoEvent(this, addSubtasksToTodosCommand.subtasksToAdd))
    }

    @CommandHandler
    fun delSubtasks(deleteSubtasksFromTodosCommand: DeleteSubtasksFromTodosCommand) {
        if(!subtasks.containsAll(deleteSubtasksFromTodosCommand.subtasksToDelete))
            throw java.lang.IllegalArgumentException("Impossible de supprimer ces subtasks de ce Todo ! Le Todo ne contient pas une/plusieurs des subtasks indiquées")
        //subtasks.removeAll(deleteSubtasksFromTodosCommand.subtasksToDelete)
        subtasks.removeIf { actualSub -> deleteSubtasksFromTodosCommand.subtasksToDelete.contains(actualSub) }
        apply(SubtasksDeletedFromTodoEvent(this, deleteSubtasksFromTodosCommand.subtasksToDelete))
    }


    @EventSourcingHandler
    fun on(todoCreatedEvent: TodoCreatedEvent){
        this.id = todoCreatedEvent.theTodo.id
        this.name = todoCreatedEvent.theTodo.name
        this.description = todoCreatedEvent.theTodo.description
        this.priority = todoCreatedEvent.theTodo.priority
        this.subtasks = todoCreatedEvent.theTodo.subtasks
    }


    @EventSourcingHandler
    fun on(todoUpdatedEvent: TodoUpdatedEvent) {
        this.name = todoUpdatedEvent.todoUpdated.name
        this.description = todoUpdatedEvent.todoUpdated.description
        this.priority = todoUpdatedEvent.todoUpdated.priority
        this.subtasks = todoUpdatedEvent.todoUpdated.subtasks
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