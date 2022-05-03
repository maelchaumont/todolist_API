package com.example.todolist.upcasters

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.function.Function

@Order(2)
@Component
public class TodoUpdatedEventNull_to_1 : SingleEventUpcaster() {

    val PAYLOAD_TYPE = "kotlin.com.example.todolist.coreapi.todo.TodoUpdatedEvent";

    override fun canUpcast(intermediateEventRepresentation : IntermediateEventRepresentation): Boolean {
        val payloadType : SerializedType = intermediateEventRepresentation.getData().getType();
        return PAYLOAD_TYPE.equals(payloadType.getName())
                && payloadType.getRevision() == null;
    }

   override fun doUpcast(intermediateEventRepresentation : IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateEventRepresentation.upcastPayload(
            SimpleSerializedType(PAYLOAD_TYPE, "1"),
            JsonNode::class.java,
            Function { event: JsonNode ->
                (event as ObjectNode).put("subtasks", "[]")
                event
            }
        );
    }
}