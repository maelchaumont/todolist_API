package com.example.todolist.saga

import com.example.todolist.saga.messagesPart.DeleteTodoV2Command
import com.example.todolist.saga.messagesPart.TodoV2CreatedEvent
import com.example.todolist.saga.messagesPart.TodoV2SagaPercentageDoneAddedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Saga(sagaStore = "mongoSagaStore")
@Document
class SagaTodoV2Deadline(){
    data class Subtask(val id: UUID, val name: String)

    @Id
    val sagaID = UUID.randomUUID()

    var todoV2ID: UUID? = null
    var percentageDone: Int = 0

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    fun on (todoV2CreatedEvent: TodoV2CreatedEvent) {
        this.todoV2ID = todoV2CreatedEvent.id
        SagaLifecycle.associateWith("todoV2ID", todoV2ID.toString())
    }

    @SagaEventHandler(associationProperty = "id")
    fun on (todoV2SagaPercentageDoneAddedEvent: TodoV2SagaPercentageDoneAddedEvent, @Autowired commandGateway: CommandGateway) {
        if(todoV2SagaPercentageDoneAddedEvent.newPercentage <= percentageDone)
            throw IllegalArgumentException("The new percentage can't be inferior to the actual one ! The actual value is ${percentageDone}")
        else if(todoV2SagaPercentageDoneAddedEvent.newPercentage >= 100) {
            //Todo done at 100%, so it is no longer usefull and can be deleted
            commandGateway.send<DeleteTodoV2Command>(DeleteTodoV2Command(todoV2ID!!))
            SagaLifecycle.end()
        } else percentageDone = todoV2SagaPercentageDoneAddedEvent.newPercentage
    }
}