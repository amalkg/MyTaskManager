package com.cns.mytaskmanager.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.model.TaskListResponse
import com.cns.mytaskmanager.data.model.Todos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    val taskList = MutableLiveData<List<Todos>>()

    fun fetchTaskList() {
        viewModelScope.launch {
            kotlin.runCatching {
                mainRepository.getTaskList()
            }.onSuccess {
                taskList.postValue(it.body()?.todos)
            }.onFailure {
                println("falied" + it.message)
            }

        }
    }
}