package com.example.todolist.saga

import com.example.todolist.saga.commandPart.TodoV2CreatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@Saga//(sagaStore = "mySagaStore")
class SagaTodoV2Deadline {

    @Transient
    private var commandGateway: CommandGateway? = null

    @Transient
    private var deadlineManager: DeadlineManager? = null

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

        SagaLifecycle.associateWith("id", todoV2ID.toString())

        deadlineManager.schedule(Duration.of(todoV2CreatedEvent.minutesLeftToUpdate.toLong(), ChronoUnit.MINUTES), "modifyDeadline")
    }

    //@SagaEventHandler(associationProperty = "todoV2ID")

    @EndSaga
    @DeadlineHandler(deadlineName = "modifyDeadline")
    fun deadlineExpired() {
        SagaLifecycle.end()
    }
}