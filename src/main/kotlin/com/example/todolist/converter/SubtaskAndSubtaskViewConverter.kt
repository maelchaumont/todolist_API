package com.example.todolist.converter

import com.example.todolist.command.Subtask
import com.example.todolist.command.Todo
import com.example.todolist.query.SubtaskView
import com.example.todolist.query.TodoView

class SubtaskAndSubtaskViewConverter() {
    fun convertSubtaskToSubtaskView(subtask: Subtask): SubtaskView {
        return SubtaskView(subtask.subtaskID!!, subtask.name!!)
    }

    fun convertSubtaskViewToSubtask(subtaskView: SubtaskView): Subtask {
        return Subtask(subtaskView.subtaskID,  subtaskView.name)
    }

    fun convertListSubtask2ListSubtaskView(listSubtask: MutableList<Subtask>): MutableList<SubtaskView> {
        val listToReturn : MutableList<SubtaskView> = mutableListOf()
        listSubtask.forEach { sub -> listToReturn.add(SubtaskView(sub.subtaskID!!, sub.name!!)) }
        return listToReturn
    }

    fun convertListSubtaskView2ListSubtask(listSubtaskView: MutableList<SubtaskView>): MutableList<Subtask> {
        val listToReturn : MutableList<Subtask> = mutableListOf()
        listSubtaskView.forEach { subView -> listToReturn.add(Subtask(subView.subtaskID, subView.name)) }
        return listToReturn
    }
}