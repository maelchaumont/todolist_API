package com.example.todolist.command

import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import com.example.todolist.coreapi.todo.*
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
    constructor(createTodoCommand: CreateTodoCommand) : this() {
        AggregateLifecycle.apply(TodoCreatedEvent(this.id!!,
                                                  this.name!!,
                                                  this.description!!,
                                                  this.priority!!,
                                                  createTodoCommand.subtasks.map { TodoCreatedEvent.Subtask(UUID.randomUUID(), it.name) }.toMutableList()))
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
    fun updateTodoInfo(updateTodoInfoCommand: UpdateTodoInfoCommand) {
        apply(TodoInfoUpdatedEvent(updateTodoInfoCommand.id, updateTodoInfoCommand.name, updateTodoInfoCommand.description))
    }

    @CommandHandler
    fun updateTodoPriority(updateTodoPriorityCommand: UpdateTodoPriorityCommand) {
        apply(TodoPriorityUpdatedEvent(updateTodoPriorityCommand.id, updateTodoPriorityCommand.priority))
    }

    @CommandHandler
    fun deleteTodo(deleteTodoCommand: DeleteTodoCommand){
        apply(TodoDeletedEvent(id as UUID))
    }

    //ajouté à la place des constructeurs annotés @CommandHandler das Subtask
    @CommandHandler
    fun addSubtask(createSubtaskCommand: CreateSubtaskCommand) {
        apply(SubtaskCreatedEvent(SubtaskCreatedEvent.Subtask(UUID.randomUUID(), createSubtaskCommand.name), this.id!!))
    }

    @CommandHandler
    fun delSubtask(deleteSubtaskCommand: DeleteSubtaskCommand) {
        apply(SubtaskDeletedEvent(deleteSubtaskCommand.subtaskToDeleteID, this.id!!))
    }


    @EventSourcingHandler
    fun on(todoCreatedEvent: TodoCreatedEvent){
        this.id = todoCreatedEvent.id
        this.name = todoCreatedEvent.name
        this.description = todoCreatedEvent.description
        this.priority = todoCreatedEvent.priority
        this.subtasks = todoCreatedEvent.subtasks.map { Subtask(it.id, it.name) }.toMutableList()
    }

    @EventSourcingHandler
    fun on(todoInfoUpdatedEvent: TodoInfoUpdatedEvent) {
        if(!name.isNullOrBlank())
            this.name = todoInfoUpdatedEvent.name
        if(!description.isNullOrBlank())
            this.description = todoInfoUpdatedEvent.description
    }

    @EventSourcingHandler
    fun on(todoPriorityUpdatedEvent: TodoPriorityUpdatedEvent) {
        this.priority = todoPriorityUpdatedEvent.priority
    }

    @EventSourcingHandler
    fun on(todoDeletedEvent: TodoDeletedEvent) {
        markDeleted()
    }

    @EventSourcingHandler
    fun on(subtaskCreatedEvent: SubtaskCreatedEvent) {
        subtasks.add(Subtask(subtaskCreatedEvent.subtask.idSubtask, subtaskCreatedEvent.subtask.name))
    }

    @EventSourcingHandler
    fun on(subtaskDeletedEvent: SubtaskDeletedEvent) {
        subtasks.removeIf { subtask -> subtask.subtaskID!!.equals(subtaskDeletedEvent.subtaskDeletedID) }
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
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        result = 31 * result + subtasks.hashCode()
        return result
    }
}