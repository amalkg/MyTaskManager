package com.cns.mytaskmanager.ui.add_update_task

import androidx.lifecycle.ViewModel
import com.cns.mytaskmanager.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddUpdateTaskViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

}