package com.cns.mytaskmanager.utils

import com.cns.mytaskmanager.Todo

class PriorityComparatorLowHigh : Comparator<Todo> {
    override fun compare(task1: Todo, task2: Todo): Int {
        val priorityOrder = listOf("Low", "Medium", "High")
        val priority1Index = priorityOrder.indexOf(task1.priority)
        val priority2Index = priorityOrder.indexOf(task2.priority)
        return priority1Index - priority2Index
    }
}