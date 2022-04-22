package com.example.todolist.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CreateTodoDTOCommand (@TargetAggregateIdentifier val name: String, val description: String, val priority: String){

    init {
        testPasseDansCreateTodoDTOCommand()
    }

    fun testPasseDansCreateTodoDTOCommand(): ResponseEntity<Any> {
        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateTodoDTOCommand

        if (name != other.name) return false
        if (description != other.description) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }

    override fun toString(): String {
        return "CreateTodoDTOCommand(name='$name', description='$description', priority='$priority')"
    }
}