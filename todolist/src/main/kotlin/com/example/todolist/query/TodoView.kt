package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.coreapi.todo.CreateRealTodoCommand
import com.example.todolist.coreapi.todo.TodoCreatedEvent
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "todolist")
class TodoView(@MongoId val id: Int,
               @Field(name = "name") var name: String,
               @Field(name = "description") var description: String,
               @Field(name = "priority") var priority: String,
               @Field(name = "subtasks") val subtasks: MutableList<Subtask>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoView

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
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + subtasks.hashCode()
        return result
    }
}