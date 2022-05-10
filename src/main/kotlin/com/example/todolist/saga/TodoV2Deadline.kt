package com.example.todolist.saga

import com.example.todolist.command.Subtask
import com.example.todolist.saga.commandPart.CreateTodoV2Command
import com.example.todolist.saga.commandPart.DeleteTodoV2Command
import com.example.todolist.saga.commandPart.TodoV2CreatedEvent
import com.example.todolist.saga.commandPart.TodoV2DeletedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import java.util.*

class TodoV2Deadline() {
    var id: UUID? = null
    var name: String? = null
    var description: String? = null
    var priority: String? = null
    var minutesLeftToUpdate: Int? = null
    var subtasks: MutableList<Subtask> = mutableListOf()

    @CommandHandler
    constructor(createTodoV2Command: CreateTodoV2Command) : this() {
        AggregateLifecycle.apply(TodoV2CreatedEvent(UUID.randomUUID(),
                                 createTodoV2Command.name,
                                 createTodoV2Command.description,
                                 createTodoV2Command.priority,
                                 createTodoV2Command.subtasks.map { TodoV2CreatedEvent.Subtask(it.id, it.name) },
                                 createTodoV2Command.timeLeftTodo))
    }

    @CommandHandler
    fun handle(deleteTodoV2Command: DeleteTodoV2Command) {
        AggregateLifecycle.apply(TodoV2DeletedEvent(this.id!!))
    }

    /*
    @EventSourcingHandler
    fun on(todoV2CreatedEvent: TodoV2CreatedEvent){
        this.id = todoV2CreatedEvent.id
        this.name = todoV2CreatedEvent.name
        this.description = todoV2CreatedEvent.description
        this.priority = todoV2CreatedEvent.priority
        this.subtasks = todoV2CreatedEvent.subtasks.map { Subtask(it.id, it.name) }.toMutableList()
        this.minutesLeftToUpdate = todoV2CreatedEvent.minutesLeftToUpdate
    }

    @EventSourcingHandler
    fun on(todoV2DeletedEvent: TodoV2DeletedEvent) {
        markDeleted()
    }
     */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoV2Deadline

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (priority != other.priority) return false
        if (minutesLeftToUpdate != other.minutesLeftToUpdate) return false
        if (subtasks != other.subtasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        result = 31 * result + (minutesLeftToUpdate ?: 0)
        result = 31 * result + subtasks.hashCode()
        return result
    }

    override fun toString(): String {
        return "TodoV2Deadline(id=$id, name=$name, description=$description, priority=$priority, minutesLeftToUpdate=$minutesLeftToUpdate, subtasks=$subtasks)"
    }
}