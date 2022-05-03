package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.converter.SubtaskAndSubtaskViewConverter
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksIDsQuery
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksQuery
import com.example.todolist.coreapi.queryMessage.FindNewSubtaskIDQuery
import com.example.todolist.coreapi.queryMessage.FindSubtasksByIDQuery
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class SubtaskProjection(@Autowired val subtaskRepository: SubtaskRepository, @Autowired val mongoOps: MongoOperations, @Autowired val mongoTemplate: MongoTemplate) {

    @EventHandler
    fun handle(subtaskCreatedEvent: SubtaskCreatedEvent) {
        //subtaskRepository.save(subtaskCreatedEvent.subtaskCreated)
        //mongoOps.save(subtaskCreatedEvent.subtaskCreated, "subtask")
        mongoTemplate.save(subtaskCreatedEvent.subtaskCreated, "subtask")
    }

    @EventHandler
    fun handle(subtaskDeletedEvent: SubtaskDeletedEvent) {
        subtaskRepository.deleteById(subtaskDeletedEvent.subtaskToDeleteID)
    }

    @QueryHandler
    fun handle(findAllSubtasksQuery: FindAllSubtasksQuery): MutableList<Subtask> {
        //return subtaskRepository.findAll()
        val listToReturn: MutableList<Subtask> = mutableListOf()
        for(subtaskView in subtaskRepository.findAll()) {
            listToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView))
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findAllSubtasksIDsQuery: FindAllSubtasksIDsQuery): MutableList<String> {
        val listToReturn: MutableList<String> = mutableListOf()
        for(subtaskView in subtaskRepository.findAll()) {
            listToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView).subtaskID.toString())
        }
        return listToReturn
    }

    @QueryHandler
    fun handle(findNewSubtaskIDQuery: FindNewSubtaskIDQuery): String {
        /*var idToReturn = 0
        while(subtaskRepository.existsById("sub$idToReturn"))
            idToReturn++
         */
        val idToReturn = subtaskRepository.findAll().last().subtaskID.substring(3).toInt()+1
        return "sub$idToReturn"
    }


    @QueryHandler
    fun handle(findSubtasksByIDQuery: FindSubtasksByIDQuery): List<Subtask> {
        val subtaskListToReturn: MutableList<Subtask> = mutableListOf()
        for(subtaskView in subtaskRepository.findAll().filter { subtask -> findSubtasksByIDQuery.subtasksIDs.contains(subtask.subtaskID) })
            subtaskListToReturn.add(SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskView))
        return subtaskListToReturn
    }
}