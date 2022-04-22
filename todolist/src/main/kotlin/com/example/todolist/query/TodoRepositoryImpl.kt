package com.example.todolist.query

import com.example.todolist.command.Todo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

class TodoRepositoryImpl : TodoRepository {
    override fun <S : Todo?> save(entity: S): S {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> saveAll(entities: MutableIterable<S>): MutableList<S> {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Optional<Todo> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableList<Todo> {
        TODO("Not yet implemented")
    }

    override fun findAll(sort: Sort): MutableList<Todo> {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> findAll(example: Example<S>): MutableList<S> {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> findAll(example: Example<S>, sort: Sort): MutableList<S> {
        TODO("Not yet implemented")
    }

    override fun findAll(pageable: Pageable): Page<Todo> {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> findAll(example: Example<S>, pageable: Pageable): Page<S> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<String>): MutableIterable<Todo> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> count(example: Example<S>): Long {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Todo) {
        TODO("Not yet implemented")
    }

    override fun deleteAllById(ids: MutableIterable<String>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<Todo>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> findOne(example: Example<S>): Optional<S> {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> exists(example: Example<S>): Boolean {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> insert(entity: S): S {
        TODO("Not yet implemented")
    }

    override fun <S : Todo?> insert(entities: MutableIterable<S>): MutableList<S> {
        TODO("Not yet implemented")
    }
}