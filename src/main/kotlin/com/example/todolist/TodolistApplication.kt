package com.example.todolist


import com.google.common.util.concurrent.AbstractScheduledService.Scheduler
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.quartz.QuartzDeadlineManager
import org.axonframework.serialization.Serializer
import org.axonframework.spring.config.AxonConfiguration
import org.axonframework.spring.saga.SpringResourceInjector
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
