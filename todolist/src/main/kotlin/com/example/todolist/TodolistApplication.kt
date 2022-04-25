package com.example.todolist

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableMongoRepositories
@EnableTransactionManagement(proxyTargetClass = true)
class TodolistApplication

fun main(args: Array<String>) {
	 runApplication<TodolistApplication>(*args)
}

/*
@Bean
fun mongoClient(): MongoClient {
	return MongoClients.create("mongodb://localhost:27017")
}

@Bean
fun mongoTemplate(): MongoTemplate {
	return MongoTemplate(mongoClient(), "todolist_db")
}
 */
