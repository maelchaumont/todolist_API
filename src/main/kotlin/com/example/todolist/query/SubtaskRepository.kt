package com.example.todolist.query

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository("repo2")
interface SubtaskRepository : MongoRepository<SubtaskView, UUID> {
}