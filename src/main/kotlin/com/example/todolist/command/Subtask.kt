package com.example.todolist.command

import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.modelling.command.EntityId
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

class Subtask() {
    @MongoId
    @EntityId
    var subtaskID: UUID? = null
    var name: String? = null

    //appelé par SubtaskAndSubtaskViewConverter
    constructor(subtaskID: UUID, name: String): this() {
        this.subtaskID = subtaskID
        this.name = name
    }

    //On veut créer/supprimer les Subtasks depuis le Todo ??
    /*
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
     */
    /*
    @EventSourcingHandler
    fun subtaskDeleted(subtaskDeletedEvent: SubtaskDeletedEvent) {
        markDeleted()
    }
    */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subtask

        if (subtaskID != other.subtaskID) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subtaskID?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Subtask(subtaskID=$subtaskID, name=$name)"
    }
}