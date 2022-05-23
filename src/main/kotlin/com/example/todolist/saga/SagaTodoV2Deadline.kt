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
import java.util.*

@Saga(sagaStore = "mongoSagaStore")
class SagaTodoV2Deadline(){
    data class Subtask(val id: UUID, val name: String)

    lateinit var todoV2ID: UUID
    var percentageDone: Int = 0

    @StartSaga
    @SagaEventHandler(associationProperty = "id") //that "id" is the one in the Event. It will be placed automatically in the Association array on MongoDB
    fun on (todoV2CreatedEvent: TodoV2CreatedEvent) {
        this.todoV2ID = todoV2CreatedEvent.id
        //Add a new field to the associative Array in MongoDB
        SagaLifecycle.associateWith("percentageDone", percentageDone)
    }

    @SagaEventHandler(associationProperty = "id")
    fun on (todoV2SagaPercentageDoneAddedEvent: TodoV2SagaPercentageDoneAddedEvent, @Autowired commandGateway: CommandGateway) {
        if(todoV2SagaPercentageDoneAddedEvent.newPercentage <= percentageDone)
            throw IllegalArgumentException("The new percentage can't be inferior to the actual one ! The actual value is $percentageDone")
        else if(todoV2SagaPercentageDoneAddedEvent.newPercentage >= 100) {
            //Todo done at 100%, so it is no longer useful and can be deleted
            commandGateway.send<DeleteTodoV2Command>(DeleteTodoV2Command(todoV2ID))
            SagaLifecycle.end()
        } else percentageDone = todoV2SagaPercentageDoneAddedEvent.newPercentage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SagaTodoV2Deadline

        if (todoV2ID != other.todoV2ID) return false
        if (percentageDone != other.percentageDone) return false

        return true
    }

    override fun toString(): String {
        return "SagaTodoV2Deadline(todoV2ID=$todoV2ID, percentageDone=$percentageDone)"
    }

    override fun hashCode(): Int {
        var result = todoV2ID.hashCode()
        result = 31 * result + percentageDone
        return result
    }
}