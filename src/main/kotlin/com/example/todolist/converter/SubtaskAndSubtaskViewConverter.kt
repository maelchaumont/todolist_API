package com.example.todolist.converter

import com.example.todolist.command.Subtask
import com.example.todolist.query.SubtaskView

class SubtaskAndSubtaskViewConverter() {
    fun convertSubtaskToSubtaskView(subtask: Subtask): SubtaskView {
        return SubtaskView(subtask.subtaskID!!, subtask.name!!)
    }

    fun convertSubtaskViewToSubtask(subtaskView: SubtaskView): Subtask {
        return Subtask(subtaskView.subtaskID, subtaskView.name)
    }
}