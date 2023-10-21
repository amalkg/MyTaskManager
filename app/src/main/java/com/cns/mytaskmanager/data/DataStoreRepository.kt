package com.cns.mytaskmanager.data

import androidx.lifecycle.LiveData
import com.cns.mytaskmanager.Todo
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getTodoList(): LiveData<List<Todo>>
    suspend fun addAllTodo(todos: List<Todo>)
    suspend fun addTodo(todos: Todo)
    suspend fun updateTodo(position: Int, todos: Todo)
    suspend fun removeTodo(position: Int)
    suspend fun clearAllTodoList()
}