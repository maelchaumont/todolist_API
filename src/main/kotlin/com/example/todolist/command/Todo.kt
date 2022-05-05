package com.example.todolist.command

import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import com.example.todolist.coreapi.todo.*
import com.example.todolist.coreapi.todoAndSubtaskInteractions.AddSubtaskToTodoCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.DeleteSubtasksFromTodosCommand
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtaskAddedToTodoEvent
import com.example.todolist.coreapi.todoAndSubtaskInteractions.SubtasksDeletedFromTodoEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class Todo constructor()  {
    @AggregateIdentifier
    var id: UUID? = null
    var name: String? = null
    var description: String? = null
    var priority: String? = null
    @AggregateMember
    var subtasks: MutableList<Subtask> = mutableListOf()

    @CommandHandler
    constructor(createRealTodoCommand: CreateRealTodoCommand) : this() {
        this.id = UUID.randomUUID()
        this.name = createRealTodoCommand.name
        this.description = createRealTodoCommand.description
        this.priority = createRealTodoCommand.priority
        this.subtasks = createRealTodoCommand.subtasks
        AggregateLifecycle.apply(TodoCreatedEvent(this))
    }

    //appelé par le TodoAndTodoViewConverter
    constructor(id: UUID, name: String, description: String, priority: String, subtasks: MutableList<Subtask>) : this() {
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
        apply(TodoDeletedEvent(id as UUID))
    }

    /*
    @CommandHandler
    fun addSubtasks(addSubtasksToTodosCommand: AddSubtaskToTodoCommand) {
        apply(SubtaskAddedToTodoEvent(this, addSubtasksToTodosCommand.subtaskToAdd))
    }

    @CommandHandler
    fun delSubtasks(deleteSubtasksFromTodosCommand: DeleteSubtasksFromTodosCommand) {
        apply(SubtasksDeletedFromTodoEvent(this, deleteSubtasksFromTodosCommand.subtasksToDelete))
    }
    */

    //ajouté à la place des constructeurs annotés @CommandHandler das Subtask
    @CommandHandler
    fun addSubtask(createSubtaskCommand: CreateSubtaskCommand) {
        val subtaskToAdd = Subtask(UUID.randomUUID(), createSubtaskCommand.name)
        subtasks.add(subtaskToAdd)
        apply(SubtaskCreatedEvent(subtaskToAdd, this))
    }

    @CommandHandler
    fun delSubtask(deleteSubtaskCommand: DeleteSubtaskCommand) {
        apply(SubtaskDeletedEvent(deleteSubtaskCommand.subtaskToDeleteID, this))
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
    fun on(subtasksAddedToTodoEvent: SubtaskAddedToTodoEvent) {
        subtasks.add(subtasksAddedToTodoEvent.subtaskAdded)
    }

    @EventSourcingHandler
    fun on(subtasksDeletedFromTodoEvent: SubtasksDeletedFromTodoEvent) {
        subtasks.removeAll(subtasksDeletedFromTodoEvent.subtasksDeleted)
    }

    @EventSourcingHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        markDeleted()
    }

    /*
    @EventSourcingHandler
    fun on(subtaskCreatedEvent: SubtaskCreatedEvent) {
        subtasks.add(subtaskCreatedEvent.subtaskCreated)
    }

    @EventSourcingHandler
    fun on(subtaskDeletedEvent: SubtaskDeletedEvent) {
        subtasks.removeIf { sub -> sub.subtaskID!!.equals(subtaskDeletedEvent.subtaskDeletedID) }
    }
     */

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
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        result = 31 * result + subtasks.hashCode()
        return result
    }
}