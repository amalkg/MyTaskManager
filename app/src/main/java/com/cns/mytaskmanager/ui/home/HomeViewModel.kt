package com.cns.mytaskmanager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.model.Todos
import com.cns.mytaskmanager.utils.capitalizeFirstLetter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    val todoListFromApi = MutableLiveData<List<Todos>>()

    fun getTodoList() = viewModelScope.launch {
        _todoList = dataRepository.getTodoList()
    }

    fun addTodoList(todos: List<Todo>) = viewModelScope.launch {
        dataRepository.addAllTodo(todos)
    }

    fun clearAllTodoList() = viewModelScope.launch {
        dataRepository.clearAllTodoList()
    }

    fun removeTodo(position: Int) = viewModelScope.launch {
        dataRepository.removeTodo(position)
    }

    fun fetchTaskList() {
        viewModelScope.launch {
            kotlin.runCatching {
                mainRepository.getTaskList()
            }.onSuccess {
                todoListFromApi.postValue(it.body()?.todos)
                clearAllTodoList()
                for (todo in it.body()?.todos!!) {
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

            }.onFailure {
                println("falied" + it.message)
            }

        }
    }
}