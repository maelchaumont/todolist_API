package com.example.todolist

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
@EnableMongoRepositories
class TodolistApplication
	var myCommandGateway: CommandGateway? = null
	var myCommandBus: CommandBus? = null

fun main(args: Array<String>) {
	val myAppContext : ConfigurableApplicationContext = runApplication<TodolistApplication>(*args)
	@Bean
	myCommandGateway = myAppContext.getBean(CommandGateway::class.java)
	@Bean
	myCommandBus = myAppContext.getBean(CommandBus::class.java)
}

@Bean
fun mongoClient(): MongoClient {
	return MongoClients.create("mongodb://localhost:27017")
}

@Bean
fun mongoTemplate(): MongoTemplate {
	return MongoTemplate(mongoClient(), "todolist_db")
}
