package com.cns.mytaskmanager.utils

import com.cns.mytaskmanager.Todo

class PriorityComparatorHighLow : Comparator<Todo> {
    override fun compare(task1: Todo, task2: Todo): Int {
        val priorityOrder = listOf("High", "Medium", "Low")
        val priority1Index = priorityOrder.indexOf(task1.priority)
        val priority2Index = priorityOrder.indexOf(task2.priority)
        return priority1Index - priority2Index
    }
}