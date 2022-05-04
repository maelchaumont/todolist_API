package com.example.todolist


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableMongoRepositories
@EnableTransactionManagement(proxyTargetClass = true)
class TodolistApplication

fun main(args: Array<String>) {
	 runApplication<TodolistApplication>(*args)
}

