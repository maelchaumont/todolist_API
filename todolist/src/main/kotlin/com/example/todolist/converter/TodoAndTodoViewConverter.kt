package com.example.todolist.converter

import com.example.todolist.command.Todo
import com.example.todolist.query.TodoView

class TodoAndTodoViewConverter {
    fun convertTodoToTodoView(theTodo: Todo): TodoView {
        return TodoView(theTodo.id!!, theTodo.name.toString(), theTodo.description.toString(), theTodo.priority.toString(), theTodo.subtasks)
    }

    fun convertTodoViewToTodo(theTodoView: TodoView): Todo {
        return Todo(theTodoView.id, theTodoView.name, theTodoView.description, theTodoView.priority, theTodoView.subtasks)
    }
}