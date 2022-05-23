package com.example.todolist.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.thoughtworks.xstream.XStream
import org.axonframework.extensions.mongo.DefaultMongoTemplate
import org.axonframework.extensions.mongo.MongoTemplate
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore
import org.axonframework.serialization.xml.XStreamSerializer
import org.bson.UuidRepresentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MyConfiguration() {

    //MONGOSAGASTORE
    @Bean
    fun mongoSagaStore(): MongoSagaStore {
        val xStream = XStream()
        xStream.allowTypesByWildcard(arrayOf("com.example.todolist.query.**",
                                             "com.example.todolist.saga.queryPart.**",
                                             "com.example.todolist.saga.SagaTodoV2Deadline"))
        return MongoSagaStore.builder()
                             .mongoTemplate(axonMongoTemplate())
                             .serializer(XStreamSerializer.builder().xStream(xStream).build())
                             .build()
    }

    @Bean
    fun axonMongoTemplate(): MongoTemplate {
        return DefaultMongoTemplate.builder()
                                    .mongoDatabase(mongoClient())
                                    .sagasCollectionName("realSagas")
                                    .build()
    }

    @Bean
    fun mongoClient(): MongoClient {
        val mongoClientSettings = MongoClientSettings.builder()
                                                     .uuidRepresentation(UuidRepresentation.STANDARD)
                                                     .applyConnectionString(ConnectionString("mongodb://localhost:27017"))
                                                     .build()
        return MongoClients.create(mongoClientSettings)
    }
}