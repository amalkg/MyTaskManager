package com.cns.mytaskmanager.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.databinding.FragmentSearchBinding
import com.cns.mytaskmanager.ui.home.HomeViewModel
import com.cns.mytaskmanager.ui.home.TaskAdapter
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModels()
    lateinit var binding: FragmentSearchBinding

    private var taskAdapter: TaskAdapter? = null

    private var todosOriginal: ArrayList<Todo> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

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

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
    }

    private fun setupObservables() {
        searchViewModel.todoList.observe(viewLifecycleOwner) { list ->
            todosOriginal = arrayListOf()
            todosOriginal.addAll(list)
            taskAdapter?.submitList(list)
        }

        searchViewModel.searchList.observe(viewLifecycleOwner) { filteredList ->
            taskAdapter?.submitList(filteredList)
        }

        // Set up the EditText for searching
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchViewModel.filterList(s.toString())
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

    }


}