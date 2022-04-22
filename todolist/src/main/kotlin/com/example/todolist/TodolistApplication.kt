package com.example.todolist

import com.example.todolist.query.TodoRepository
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableMongoRepositories
class TodolistApplication

fun main(args: Array<String>) {
	runApplication<TodolistApplication>(*args)
}

@Bean
fun mongoClient(): MongoClient {
	return MongoClients.create("mongodb://localhost:27017")
}

@Bean
fun mongoTemplate(): MongoTemplate {
	return MongoTemplate(mongoClient(), "todolist_db")
}
