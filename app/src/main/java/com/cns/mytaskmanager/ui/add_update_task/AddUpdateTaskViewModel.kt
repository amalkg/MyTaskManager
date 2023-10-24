package com.cns.mytaskmanager.ui.add_update_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.PreferenceDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateTaskViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val dataRepository: DataStoreRepository,
    private val preferenceDataRepository: PreferenceDataRepositoryImpl
) :
    ViewModel() {
    init {
        getCategoryList()
    }

    private lateinit var _categoryList: LiveData<String?>

    val categoryList: LiveData<String?> = _categoryList

    val titleLiveData = MutableLiveData<String>()
    val categoryLiveData = MutableLiveData<String>()
    val noteLiveData = MutableLiveData<String>()
    val dateLiveData = MutableLiveData<String>()
    val priorityLiveData = MutableLiveData<String>()
    val isValidLiveData = MutableLiveData<Boolean>()

    /**
     * Validation for add and update function
     */
    fun validateForm() {
        val title = titleLiveData.value.orEmpty()
        val category = categoryLiveData.value.orEmpty()
        val note = noteLiveData.value.orEmpty()
        val date = dateLiveData.value.orEmpty()
        val priority = priorityLiveData.value.orEmpty()

        val isTitleValid = title.isNotEmpty()
        val isCategoryValid = category.isNotEmpty()
        val isNoteValid = note.isNotEmpty()
        val isDateValid = date.isNotEmpty()
        val isPriorityValid = priority.isNotEmpty()

        isValidLiveData.value =
            isTitleValid && isCategoryValid && isNoteValid && isDateValid && isPriorityValid
    }

    /**
     * Add single task to the datastore
     */
    fun addTodo(todos: Todo) = viewModelScope.launch {
        dataRepository.addTodo(todos)
    }

    /**
     * Remove single task from the datastore
     */
    fun removeTodo(position: Int) = viewModelScope.launch {
        dataRepository.removeTodo(position)
    }

    /**
     * Update single task in the datastore
     */
    fun updateTodo(position: Int, todo: Todo) = viewModelScope.launch {
        dataRepository.updateTodo(position, todo)
    }

    /**
     * Get all categories saved in the datastore
     */
    fun getCategoryList() = viewModelScope.launch {
        _categoryList = preferenceDataRepository.getCategoryList()
    }
}