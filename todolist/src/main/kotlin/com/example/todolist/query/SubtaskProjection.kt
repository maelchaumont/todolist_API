package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SubtaskProjection(@Autowired val subtaskRepository: SubtaskRepository) {

    @EventHandler
    fun handle(subtaskCreatedEvent: SubtaskCreatedEvent) {
        subtaskRepository.save(subtaskCreatedEvent.subtaskCreated)
    }

    @EventHandler
    fun handle(subtaskDeletedEvent: SubtaskDeletedEvent) {
        subtaskRepository.deleteById(subtaskDeletedEvent.subtaskToDeleteID)
    }

    @QueryHandler
    fun handle(findNewSubtaskIDQuery: FindNewSubtaskIDQuery): String {
        var idToReturn = 0
        while(subtaskRepository.existsById("sub$idToReturn"))
            idToReturn++
        return "sub$idToReturn"
    }

    @QueryHandler
    fun handle(findSubtasksByIDQuery: FindSubtasksByIDQuery): List<Subtask> {
        return subtaskRepository.findAll().filter { subtask -> findSubtasksByIDQuery.subtasksIDs.contains(subtask.subtaskID) }
    }
}