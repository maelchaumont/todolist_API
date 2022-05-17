package com.example.todolist.config

import org.springframework.context.annotation.Configuration


@Configuration
class MyConfiguration() {
    /*
    @Bean
    fun mongoSagaStore(): MongoSagaStore {
        return MongoSagaStore.builder().mongoTemplate(axonMongoTemplate()).build()
    }

    @Bean
    fun axonMongoTemplate(): MongoTemplate {
        return DefaultMongoTemplate.builder().mongoDatabase(mongoClient()).build()
    }

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create("mongodb://localhost:27017/todolist_db")
        //return MongoClient(MongoClientURI("mongodb://localhost:27017/todolist_db"))
    }
     */
}