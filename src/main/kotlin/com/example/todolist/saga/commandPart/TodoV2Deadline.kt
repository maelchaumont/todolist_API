package com.example.todolist.saga.commandPart

import com.example.todolist.command.Subtask
import com.example.todolist.saga.messagesPart.*
import com.example.todolist.simpleDeadlineManager
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateLifecycle.markDeleted
import org.axonframework.spring.config.AxonConfiguration
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.properties.Delegates

@Aggregate
class TodoV2Deadline() {
    @AggregateIdentifier
    lateinit var id: UUID
    lateinit var name: String
    lateinit var description: String
    lateinit var priority: String
    lateinit var creationDate: LocalDateTime
    var minutesBeforeUpdateImpossible by Delegates.notNull<Int>()
    var nbUpdates by Delegates.notNull<Int>()
    var isLocked by Delegates.notNull<Boolean>()
    var subtasks: MutableList<Subtask> = mutableListOf()

    @CommandHandler
    constructor(createTodoV2Command: CreateTodoV2Command, @Autowired axonConfiguration: AxonConfiguration) : this() {
        val minutesBeforeUpdateImpossible = Random().nextInt(10)
        AggregateLifecycle.apply(TodoV2CreatedEvent(UUID.randomUUID(),
                                 createTodoV2Command.name,
                                 createTodoV2Command.description,
                                 createTodoV2Command.priority,
                                 createTodoV2Command.subtasks.map { TodoV2CreatedEvent.Subtask(UUID.randomUUID(), it.name) },
                                 createTodoV2Command.creationDate,
                                 minutesBeforeUpdateImpossible))

        simpleDeadlineManager(axonConfiguration).schedule(Duration.ofMinutes(minutesBeforeUpdateImpossible.toLong()), "myDeadline")
    }

    @CommandHandler
    fun handle(deleteTodoV2Command: DeleteTodoV2Command) {
        AggregateLifecycle.apply(TodoV2DeletedEvent(this.id))
    }

    @CommandHandler
    fun updateTodoInfo(updateTodoV2InfoCommand: UpdateTodoV2InfoCommand) {
        if(!isLocked) {
            AggregateLifecycle.apply(
                TodoV2InfoUpdatedEvent(
                    updateTodoV2InfoCommand.id,
                    updateTodoV2InfoCommand.name,
                    updateTodoV2InfoCommand.description
                )
            )
        } else throw IllegalStateException("This todo is now locked, it cannot be updated anymore !")
    }

    @CommandHandler
    fun updateTodoPriority(updateTodoV2PriorityCommand: UpdateTodoV2PriorityCommand) {
        if(!isLocked) {
            AggregateLifecycle.apply(
                TodoV2PriorityUpdatedEvent(
                    updateTodoV2PriorityCommand.id,
                    updateTodoV2PriorityCommand.priority
                )
            )
        } else throw IllegalStateException("This todo is now locked, it cannot be updated anymore !")
    }

    @CommandHandler
    fun addPercentageDoneToSaga(addToPercentageDoneTodoV2SagaCommand: AddToPercentageDoneTodoV2SagaCommand) {
        AggregateLifecycle.apply(TodoV2SagaPercentageDoneAddedEvent(id, addToPercentageDoneTodoV2SagaCommand.newPercentage))
    }

    @EventSourcingHandler
    fun on(todoV2CreatedEvent: TodoV2CreatedEvent){
        this.id = todoV2CreatedEvent.id
        this.name = todoV2CreatedEvent.name
        this.description = todoV2CreatedEvent.description
        this.priority = todoV2CreatedEvent.priority
        this.subtasks = todoV2CreatedEvent.subtasks.map { Subtask(it.id, it.name) }.toMutableList()
        this.creationDate = todoV2CreatedEvent.creationDate
        this.minutesBeforeUpdateImpossible = todoV2CreatedEvent.minutesLeftToUpdate
        this.nbUpdates = 0
        this.isLocked = false
    }

    @EventSourcingHandler
    fun on(todoV2InfoUpdatedEvent: TodoV2InfoUpdatedEvent) {
        nbUpdates++
    }

    @EventSourcingHandler
    fun on(todoV2PriorityUpdatedEvent: TodoV2PriorityUpdatedEvent) {
        nbUpdates++
    }

    @EventSourcingHandler
    fun on(todoV2DeletedEvent: TodoV2DeletedEvent) {
        markDeleted()
    }


    @DeadlineHandler(deadlineName = "myDeadline")
    fun handleDeadline(){
        isLocked = true
        AggregateLifecycle.apply(TodoV2UpdateLockedEvent(id))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoV2Deadline

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (priority != other.priority) return false
        if (creationDate != other.creationDate) return false
        if (minutesBeforeUpdateImpossible != other.minutesBeforeUpdateImpossible) return false
        if (nbUpdates != other.nbUpdates) return false
        if (isLocked != other.isLocked) return false
        if (subtasks != other.subtasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + minutesBeforeUpdateImpossible
        result = 31 * result + nbUpdates
        result = 31 * result + isLocked.hashCode()
        result = 31 * result + subtasks.hashCode()
        return result
    }

    override fun toString(): String {
        return "TodoV2Deadline(id=$id, name='$name', description='$description', priority='$priority', creationDate=$creationDate, minutesBeforeUpdateImpossible=$minutesBeforeUpdateImpossible, nbUpdates=$nbUpdates, isLocked=$isLocked, subtasks=$subtasks)"
    }
}