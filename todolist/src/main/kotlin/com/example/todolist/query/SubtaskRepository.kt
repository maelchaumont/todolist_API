package com.example.todolist.query

import com.example.todolist.command.Subtask
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository("repo2")
interface SubtaskRepository : MongoRepository<Subtask, String> {
}