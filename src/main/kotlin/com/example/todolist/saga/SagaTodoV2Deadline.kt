package com.example.todolist.saga

import com.example.todolist.saga.messagesPart.TodoV2CreatedEvent
import com.example.todolist.saga.messagesPart.TodoV2DeletedEvent
import com.example.todolist.saga.messagesPart.TodoV2InfoUpdatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore
import org.axonframework.modelling.command.EntityId
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.modelling.saga.repository.AnnotatedSagaRepository
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@Saga
data class SagaTodoV2Deadline(@Autowired val mongoSagaStore: MongoSagaStore, @Autowired val annotatedSagaRepository: AnnotatedSagaRepository<TodoV2Deadline>){
    data class TodoV2Deadline(val id: UUID,
                              val name: String,
                              val description: String,
                              val priority: String,
                              val minutesLeftToUpdate: Int,
                              val subtasks: List<Subtask>)

    data class Subtask(val id: UUID, val name: String)

    @Transient
    private var commandGateway: CommandGateway? = null

    @Transient
    private var deadlineManager: DeadlineManager? = null

    @EntityId
    val sagaID = UUID.randomUUID()

    var todoV2ID: UUID? = null
    var name: String? = null
    var description: String? = null
    var priority: String? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    fun on (todoV2CreatedEvent: TodoV2CreatedEvent, @Autowired commandGateway: CommandGateway, @Autowired deadlineManager: DeadlineManager) {
        this.commandGateway = commandGateway
        this.deadlineManager = deadlineManager
        this.todoV2ID = todoV2CreatedEvent.id
        this.name = todoV2CreatedEvent.name
        this.description = todoV2CreatedEvent.description
        this.priority = todoV2CreatedEvent.priority

        SagaLifecycle.associateWith("id", todoV2ID.toString())

        //add this specific instance of saga to the AnnotatedSagaRepository
        //annotatedSagaRepository::doCreateInstance

        deadlineManager.schedule(Duration.of(todoV2CreatedEvent.minutesLeftToUpdate.toLong(), ChronoUnit.MINUTES), "modifyDeadline")
    }

    @SagaEventHandler(associationProperty = "id")
    fun on (todoV2InfoUpdatedEvent: TodoV2InfoUpdatedEvent, @Autowired commandGateway: CommandGateway) {
        //annotatedSagaRepository.createInstance(sagaID.toString(), )
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    fun handle(todoV2DeletedEvent: TodoV2DeletedEvent) {
        SagaLifecycle.end()
    }

    @EndSaga
    @DeadlineHandler(deadlineName = "modifyDeadline")
    fun deadlineExpired() {
        SagaLifecycle.end()
    }
}