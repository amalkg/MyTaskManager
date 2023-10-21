package com.cns.mytaskmanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.model.Todos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataRepository: DataStoreRepository
) :
    ViewModel() {
    init {
        getTodoList()
    }

    private lateinit var _todoList: LiveData<List<Todo>>

    val todoList: LiveData<List<Todo>> = _todoList

    val searchList = MutableLiveData<List<Todo>>()

    fun getTodoList() = viewModelScope.launch {
        _todoList = dataRepository.getTodoList()
    }

    fun filterList(query: String) {
        searchList.value = _todoList.value?.filter {
            it.title.contains(query, ignoreCase = true) //
        }
    }
}