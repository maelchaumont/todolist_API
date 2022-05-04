package com.example.todolist.query

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.converter.SubtaskAndSubtaskViewConverter
import com.example.todolist.converter.TodoAndTodoViewConverter
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksIDsQuery
import com.example.todolist.coreapi.queryMessage.FindAllSubtasksQuery
import com.example.todolist.coreapi.queryMessage.FindSubtasksByIDQuery
import com.example.todolist.coreapi.subtask.SubtaskCreatedEvent
import com.example.todolist.coreapi.subtask.SubtaskDeletedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.QueryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class SubtaskProjection(@Autowired val subtaskRepository: SubtaskRepository,
                        @Autowired val todoRepository: TodoRepository,
                        @Autowired val mongoTemplate: MongoTemplate,
                        @Autowired val queryGateway: QueryGateway) {

    @EventHandler
    fun handle(subtaskCreatedEvent: SubtaskCreatedEvent) {
        mongoTemplate.save(subtaskCreatedEvent.subtaskCreated, "subtask")
    }

    @EventHandler
    fun handle(subtaskDeletedEvent: SubtaskDeletedEvent) {
        val subtaskToDelete: Subtask = SubtaskAndSubtaskViewConverter().convertSubtaskViewToSubtask(subtaskRepository.findById(subtaskDeletedEvent.subtaskToDeleteID).get())
        todoRepository.findAll().forEach {
            todoView -> val actualTodo: Todo = TodoAndTodoViewConverter().convertTodoViewToTodo(todoView)
                        if(actualTodo.subtasks.contains(subtaskToDelete)) {
                            actualTodo.subtasks.remove(subtaskToDelete)
                            todoRepository.save(TodoAndTodoViewConverter().convertTodoToTodoView(actualTodo))
                        }
        }
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