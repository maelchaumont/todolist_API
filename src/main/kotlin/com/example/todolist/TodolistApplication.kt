package com.example.todolist

import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.SimpleDeadlineManager
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableMongoRepositories
@EnableTransactionManagement(proxyTargetClass = true)
class TodolistApplication

fun main(args: Array<String>) {
	 runApplication<TodolistApplication>(*args)
}


@Bean
fun simpleDeadlineManager(configuration: AxonConfiguration): SimpleDeadlineManager {
	return SimpleDeadlineManager.builder().scopeAwareProvider(ConfigurationScopeAwareProvider(configuration)).build()
}
