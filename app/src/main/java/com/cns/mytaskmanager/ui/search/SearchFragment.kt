package com.cns.mytaskmanager.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.core.BaseFragment
import com.cns.mytaskmanager.data.model.Todos
import com.cns.mytaskmanager.databinding.FragmentHomeBinding
import com.cns.mytaskmanager.databinding.FragmentSearchBinding
import com.cns.mytaskmanager.ui.home.HomeFragmentDirections
import com.cns.mytaskmanager.ui.home.HomeViewModel
import com.cns.mytaskmanager.ui.home.TaskAdapter
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.safeNavigate
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private var taskAdapter: TaskAdapter? = null

    private var todosOriginal: ArrayList<Todo> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSearch.doOnNextLayout {
            it.requestFocus()
            showKeyboard(it)
        }
        setupRecyclerView()
        setupObservables()
        setupClickListeners()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun setupViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservables() {
        viewModel.todoList.observe(viewLifecycleOwner) { list ->
            todosOriginal = arrayListOf()
            todosOriginal.addAll(list)
            taskAdapter?.submitList(list)
            taskAdapter?.notifyDataSetChanged()
        }

        viewModel.searchList.observe(viewLifecycleOwner) { filteredList ->
            taskAdapter?.submitList(filteredList)
            taskAdapter?.notifyDataSetChanged()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { item ->
            onClickTask(item)
        }
        binding.recyclerviewTaskList.apply {
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = taskAdapter
        }
    }

    private fun onClickTask(item: Todo) {
        binding.etSearch.setText("")
        hideKeyboard()
        val pos = todosOriginal.indexOf(item)
        val todoItem = Todos(
            id = item.id,
            title = item.title,
            category = item.category,
            todo = item.todo,
            completed = item.completed,
            userId = item.userId,
            date = item.date,
            priority = item.priority,
        )
        safeNavigate(
            SearchFragmentDirections
                .actionSearchFragmentToAddUpdateTaskFragment(
                    todoItem,
                    pos
                )
        )
    }


}