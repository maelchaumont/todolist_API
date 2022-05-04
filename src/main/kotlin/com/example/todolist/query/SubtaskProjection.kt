package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.converter.SubtaskAndSubtaskViewConverter
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksIDsQuery
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksQuery
import com.example.todolist.coreapi.queryMessage.FindSubtasksByIDQuery
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class SubtaskProjection(@Autowired val subtaskRepository: SubtaskRepository, @Autowired val mongoTemplate: MongoTemplate) {

    @EventHandler
    fun handle(subtaskCreatedEvent: SubtaskCreatedEvent) {
        mongoTemplate.save(subtaskCreatedEvent.subtaskCreated, "subtask")
    }

    @EventHandler
    fun handle(subtaskDeletedEvent: SubtaskDeletedEvent) {
        subtaskRepository.deleteById(subtaskDeletedEvent.subtaskToDeleteID)
    }

    @QueryHandler
    fun handle(findAllSubtasksQuery: FindAllSubtasksQuery): MutableList<Subtask> {
        val listToReturn: MutableList<Subtask> = mutableListOf()
        for(subtaskView in subtaskRepository.findAll()) {
            listToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView))
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findAllSubtasksIDsQuery: FindAllSubtasksIDsQuery): MutableList<UUID> {
        val listToReturn: MutableList<UUID> = mutableListOf()
        for (subtaskView in subtaskRepository.findAll()) {
            listToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView).subtaskID as UUID)
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findSubtasksByIDQuery: FindSubtasksByIDQuery): List<Subtask> {
        val subtaskListToReturn: MutableList<Subtask> = mutableListOf()
        for(subtaskView in subtaskRepository.findAll().filter { subtask -> findSubtasksByIDQuery.subtasksIDs.contains(subtask.subtaskID)})
            subtaskListToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView))
        return subtaskListToReturn
    }
}