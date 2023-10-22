package com.cns.mytaskmanager.data

import androidx.lifecycle.LiveData
import com.cns.mytaskmanager.Todo

interface DataStoreRepository {
    /**
     * Get all tasks saved in the datastore
     */
    suspend fun getTodoList(): LiveData<List<Todo>>

    /**
     * Add all tasks to datastore from server
     */
    suspend fun addAllTodo(todos: List<Todo>)

    /**
     * Add single task to the datastore
     */
    suspend fun addTodo(todos: Todo)

    /**
     * Update single task in the datastore
     */
    suspend fun updateTodo(position: Int, todos: Todo)

    /**
     * Remove single task from the datastore
     */
    suspend fun removeTodo(position: Int)

    /**
     * Remove all tasks from the datastore
     */
    suspend fun clearAllTodoList()
}