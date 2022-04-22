package com.example.todolist.query

import com.example.todolist.command.Todo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.scheduling.annotation.EnableAsync
import java.util.*

//@EnableCaching(proxyTargetClass = true)
//@EnableAsync(proxyTargetClass = true)
interface TodoRepository: MongoRepository<Todo, String> {}