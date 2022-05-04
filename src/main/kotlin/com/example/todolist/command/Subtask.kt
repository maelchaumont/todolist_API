package com.example.todolist.command

import com.example.todolist.coreapi.subtask.CreateSubtaskCommand
import com.example.todolist.coreapi.subtask.DeleteSubtaskCommand
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Aggregate
class Subtask() {
    @AggregateIdentifier
    @MongoId
    var subtaskID: UUID? = null
    var name: String? = null

    //appelé par SubtaskAndSubtaskViewConverter
    constructor(subtaskID: UUID, name: String): this() {
        this.subtaskID = subtaskID
        this.name = name
    }

    @CommandHandler
    constructor(createSubtaskCommand: CreateSubtaskCommand) : this() {
        this.subtaskID = UUID.randomUUID()
        this.name = createSubtaskCommand.name
        AggregateLifecycle.apply(SubtaskCreatedEvent(this))
    }

    @CommandHandler
    fun subtaskToDelete(deleteSubtaskCommand: DeleteSubtaskCommand) {
        AggregateLifecycle.apply(SubtaskDeletedEvent(this.subtaskID!!))
    }

    @EventSourcingHandler
    fun subtaskCreated(subtaskCreatedEvent: SubtaskCreatedEvent) {
        this.subtaskID = subtaskCreatedEvent.subtaskCreated.subtaskID
        this.name = subtaskCreatedEvent.subtaskCreated.name
    }

    @EventSourcingHandler
    fun subtaskDeleted(subtaskDeletedEvent: SubtaskDeletedEvent) {
        markDeleted()
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subtask

        if (subtaskID != other.subtaskID) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subtaskID.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Subtask(subtaskID='$subtaskID', name='$name')"
    }
}