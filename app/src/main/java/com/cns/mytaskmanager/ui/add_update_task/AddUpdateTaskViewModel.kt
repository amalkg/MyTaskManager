package com.cns.mytaskmanager.ui.add_update_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateTaskViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataRepository: DataStoreRepository
) :
    ViewModel() {
    fun addTodo(todos: Todo) = viewModelScope.launch {
        dataRepository.addTodo(todos)
    }

    fun removeTodo(position: Int) = viewModelScope.launch {
        dataRepository.removeTodo(position)
    }

    fun updateTodo(position: Int, todo: Todo) = viewModelScope.launch {
        dataRepository.updateTodo(position, todo)
    }

}