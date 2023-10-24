package com.cns.mytaskmanager.ui.add_category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.MainRepository
import com.cns.mytaskmanager.data.PreferenceDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
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

    val categoryLiveData = MutableLiveData<String>()
    val isValidLiveData = MutableLiveData<Boolean>()

    fun validateForm() {
        val category = categoryLiveData.value.orEmpty()

        val isCategoryValid = category.isNotEmpty()

        isValidLiveData.value = isCategoryValid
    }

    /**
     * Get all categories saved in the datastore
     */
    fun getCategoryList() = viewModelScope.launch {
        _categoryList = preferenceDataRepository.getCategoryList()
    }

    /**
     * Add categories to datastore
     */
    fun saveCategoryList(categoryList: String) = viewModelScope.launch {
        preferenceDataRepository.saveCategoryList(categoryList)
    }

}