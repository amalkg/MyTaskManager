package com.cns.mytaskmanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.TodoList
import javax.inject.Inject

class DefaultDataRepository @Inject constructor(
    private val context: Context,
    private val todoListDataStore: DataStore<TodoList>
) : DataStoreRepository {
    /**
     * Get all tasks saved in the datastore
     */
    override suspend fun getTodoList(): LiveData<List<Todo>> {
        return todoListDataStore.data.asLiveData()
            .switchMap { MutableLiveData(it.todoList) }
    }

    /**
     * Add all tasks to datastore from server
     */
    override suspend fun addAllTodo(todos: List<Todo>) {
        todoListDataStore.updateData { todo: TodoList ->
            todo.toBuilder().addAllTodo(todos).build()
        }
    }

    /**
     * Add single task to the datastore
     */
    override suspend fun addTodo(todos: Todo) {
        todoListDataStore.updateData { todo: TodoList ->
            todo.toBuilder().addTodo(todos).build()
        }
    }

    /**
     * Update single task in the datastore
     */
    override suspend fun updateTodo(position: Int, todos: Todo) {
        todoListDataStore.updateData { todo: TodoList ->
            todo.toBuilder().setTodo(position, todos).build()
        }
    }

    /**
     * Remove single task from the datastore
     */
    override suspend fun removeTodo(position: Int) {
        todoListDataStore.updateData { todo: TodoList ->
            todo.toBuilder().removeTodo(position).build()
        }
    }

    /**
     * Remove all tasks from the datastore
     */
    override suspend fun clearAllTodoList() {
        todoListDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}