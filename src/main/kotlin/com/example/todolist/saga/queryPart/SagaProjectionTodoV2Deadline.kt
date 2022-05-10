package com.example.todolist.saga.queryPart

import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore
import org.springframework.beans.factory.annotation.Autowired

data class SagaProjectionTodoV2Deadline(@Autowired val mongoSagaStore: MongoSagaStore)