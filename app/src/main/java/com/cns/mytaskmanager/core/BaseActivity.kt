package com.cns.mytaskmanager.core

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewBinding()
        setContentView(binding.root)
    }

    abstract fun getViewBinding(): VB
}

