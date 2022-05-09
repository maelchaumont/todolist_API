package com.example.todolist.converter

import com.example.todolist.command.Todo
import com.example.todolist.query.TodoView

class TodoAndTodoViewConverter {
    fun convertTodoToTodoView(theTodo: Todo): TodoView {
        return TodoView(theTodo.id!!, theTodo.name!!, theTodo.description!!, theTodo.priority!!,  SubtaskAndSubtaskViewConverter().convertListSubtask2ListSubtaskView(theTodo.subtasks))
    }

    fun convertTodoViewToTodo(theTodoView: TodoView): Todo {
        return Todo(theTodoView.id, theTodoView.name, theTodoView.description, theTodoView.priority, SubtaskAndSubtaskViewConverter().convertListSubtaskView2ListSubtask(theTodoView.subtasks))
    }

    fun convertListTodo2ListTodoView(listTodo: MutableList<Todo>): MutableList<TodoView> {
        val listToReturn : MutableList<TodoView> = mutableListOf()
        listTodo.forEach { todo -> listToReturn.add(TodoView(todo.id!!, todo.name!!, todo.description!!, todo.priority!!, SubtaskAndSubtaskViewConverter().convertListSubtask2ListSubtaskView(todo.subtasks))) }
        return listToReturn
    }

    fun convertListTodoView2ListTodo(listTodoView: MutableList<TodoView>): MutableList<Todo> {
        val listToReturn : MutableList<Todo> = mutableListOf()
        listTodoView.forEach { todoView -> listToReturn.add(Todo(todoView.id, todoView.name, todoView.description, todoView.priority, SubtaskAndSubtaskViewConverter().convertListSubtaskView2ListSubtask(todoView.subtasks))) }
        return listToReturn
    }
}