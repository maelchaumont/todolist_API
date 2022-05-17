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

/*
@Bean
fun mySecureXStream(): XStream {
	val xStream = XStream()
	xStream.allowTypesByWildcard(arrayOf("com.example.todolist.command.**",
										 "com.example.todolist.query.**",
										 "com.example.todolist.saga.queryPart.**"))
	return xStream
}
*/
