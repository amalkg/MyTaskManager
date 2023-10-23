package com.cns.mytaskmanager.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val classVM = type.actualTypeArguments[1] as Class<VM>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModelLazy(classVM.kotlin, { viewModelStore }).value
        setupViews()
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun getViewModelClass(): Class<VM>

    abstract fun setupViews()
}
