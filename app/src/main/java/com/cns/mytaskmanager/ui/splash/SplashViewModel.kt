package com.cns.mytaskmanager.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cns.mytaskmanager.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    val delay = MutableLiveData<Boolean>()

    init {
        splashDelay()
    }

    private fun splashDelay() {
        viewModelScope.launch {
            kotlin.runCatching {
                delay(2000)
            }.onSuccess {
                delay.value = true
            }.onFailure {

            }
        }
    }
}