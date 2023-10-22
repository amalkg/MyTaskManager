package com.cns.mytaskmanager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.BaseResult
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.model.TaskListResponse
import com.cns.mytaskmanager.utils.capitalizeFirstLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataRepository: DataStoreRepository
) :
    ViewModel() {

    init {
        getTodoList()
    }

    private lateinit var _todoList: LiveData<List<Todo>>

    val todoList: LiveData<List<Todo>> = _todoList

    private val _todoListFromApi = MutableLiveData<BaseResult<Response<TaskListResponse>>>()
    val todoListFromApi: LiveData<BaseResult<Response<TaskListResponse>>> = _todoListFromApi

    /**
     * Get all tasks saved in the datastore
     */
    fun getTodoList() = viewModelScope.launch {
        _todoList = dataRepository.getTodoList()
    }

    /**
     * Add all tasks to datastore from server
     */
    fun addTodoList(todos: List<Todo>) = viewModelScope.launch {
        dataRepository.addAllTodo(todos)
    }

    /**
     * Remove all tasks from the datastore
     */
    fun clearAllTodoList() = viewModelScope.launch {
        dataRepository.clearAllTodoList()
    }

    /**
     * Remove single task from the datastore
     */
    fun removeTodo(position: Int) = viewModelScope.launch {
        dataRepository.removeTodo(position)
    }

    /**
     * Get all tasks from the server
     */
    fun fetchTaskList() {
        viewModelScope.launch(Dispatchers.IO) {
            _todoListFromApi.postValue(BaseResult.Loading)
            try {
                val result = mainRepository.getTaskList()
                _todoListFromApi.postValue(BaseResult.Success(result))
                clearAllTodoList()
                for (todo in result.body()?.todos!!) {
                    println(todo.title)
                    addTodoList(
                        listOf(
                            Todo.newBuilder()
                                .setId(todo.id)
                                .setTitle(todo.title)
                                .setCategory(capitalizeFirstLetter(todo.category))
                                .setTodo(todo.todo)
                                .setCompleted(todo.completed)
                                .setUserId(todo.userId)
                                .setDate(todo.date)
                                .setPriority(todo.priority)
                                .build()
                        )
                    )
                }

            } catch (e: Exception) {
                _todoListFromApi.postValue(BaseResult.Error(e))
            }
        }
    }
}