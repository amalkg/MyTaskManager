package com.cns.mytaskmanager.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.core.BaseFragment
import com.cns.mytaskmanager.data.BaseResult
import com.cns.mytaskmanager.data.model.Todos
import com.cns.mytaskmanager.databinding.FragmentHomeBinding
import com.cns.mytaskmanager.utils.PriorityComparatorHighLow
import com.cns.mytaskmanager.utils.PriorityComparatorLowHigh
import com.cns.mytaskmanager.utils.bottom_sheet.BottomSheetFilterListDialogFragment
import com.cns.mytaskmanager.utils.bottom_sheet.BottomSheetSortListDialogFragment
import com.cns.mytaskmanager.utils.hide
import com.cns.mytaskmanager.utils.isNetworkAvailable
import com.cns.mytaskmanager.utils.safeNavigate
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.show
import com.cns.mytaskmanager.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(),
    BottomSheetFilterListDialogFragment.OnItemClickListener,
    BottomSheetSortListDialogFragment.OnItemClickListener {

    private var taskAdapter: TaskAdapter? = null

    private var todosOriginal: ArrayList<Todo> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservables()
        setupClickListeners()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun setupViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupClickListeners() {
        binding.imageAdd.setCustomClickListener {
            safeNavigate(
                HomeFragmentDirections
                    .actionHomeFragmentToAddUpdateTaskFragment(null, 0)
            )
        }

        binding.imageThreeDot.setCustomClickListener {
            showPopUpMenu()
        }

        binding.layoutFilter.setCustomClickListener {
            val bottomSheetDialog = BottomSheetFilterListDialogFragment(this)
            bottomSheetDialog.show(childFragmentManager, "BottomSheetDialog")
        }

        binding.layoutSort.setCustomClickListener {
            val bottomSheetDialog = BottomSheetSortListDialogFragment(this)
            bottomSheetDialog.show(childFragmentManager, "BottomSheetDialog")
        }

        binding.etSearch.setCustomClickListener {
            safeNavigate(
                HomeFragmentDirections
                    .actionHomeFragmentToSearchFragment()
            )
        }
    }


    private fun showPopUpMenu() {
        val popupMenu: PopupMenu? = context?.let { PopupMenu(it, binding.imageThreeDot) }
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_all ->
                    replaceItems(todosOriginal)

                R.id.action_completed ->
                    replaceItems(getListByStatus(true))

                R.id.action_pending ->
                    replaceItems(getListByStatus(false))
            }
            true
        }
        popupMenu?.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservables() {
        viewModel.todoList.observe(viewLifecycleOwner) { list ->
            todosOriginal = arrayListOf()
            todosOriginal.addAll(list)
            if (list.isNullOrEmpty()) {
                if (isNetworkAvailable(requireContext())) {
                    viewModel.fetchTaskList()
                } else {
                    requireContext().showToast(getString(R.string.please_check_your_internet_connection))
                    binding.layoutNoData.show()
                    binding.progressIndicator.hide()
                }
            } else {
                binding.layoutNoData.hide()
                binding.recyclerviewTaskList.show()
                binding.progressIndicator.hide()
                taskAdapter?.submitList(list)
                taskAdapter?.notifyDataSetChanged()
            }
        }

        viewModel.todoListFromApi.observe(viewLifecycleOwner) {
            when (it) {
                is BaseResult.Success -> {
                    binding.progressIndicator.hide()
                    binding.recyclerviewTaskList.show()
                }

                is BaseResult.Error -> {
                    println(it.exception)
                    binding.layoutNoData.show()
                }

                is BaseResult.Loading -> {
                    binding.progressIndicator.show()
                    binding.recyclerviewTaskList.hide()
                    binding.layoutNoData.hide()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter({ item ->
            onClickTask(item)
        }, { deleteItem ->
            onDeleteTask(deleteItem)
        })
        binding.recyclerviewTaskList.apply {
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = taskAdapter
        }
    }

    private fun onDeleteTask(deleteItem: Todo) {
        showDeleteDialog(deleteItem)
    }

    private fun showDeleteDialog(deleteItem: Todo) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_task))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_the_task))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.removeTodo(todosOriginal.indexOf(deleteItem))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun onClickTask(item: Todo) {
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
            HomeFragmentDirections
                .actionHomeFragmentToAddUpdateTaskFragment(
                    todoItem,
                    pos
                )
        )
    }

    /**
     * Filter all tasks by Category
     */
    private fun getListByCategory(category: String): List<Todo> =
        todosOriginal.filter { it.category == category }

    /**
     * Filter all tasks by Status
     */
    private fun getListByStatus(status: Boolean): List<Todo> =
        todosOriginal.filter { it.completed == status }

    /**
     * Sort all tasks by Priority(High to Low)
     */
    private fun getSortedListByPriorityHighLow(): List<Todo> =
        todosOriginal.sortedWith(PriorityComparatorHighLow())

    /**
     * Sort all tasks by Priority(Low to High)
     */
    private fun getSortedListByPriorityLowHigh(): List<Todo> =
        todosOriginal.sortedWith(PriorityComparatorLowHigh())

    /**
     * Sort all tasks by Date
     */
    private fun getSortedListByDate(): List<Todo> = todosOriginal.sortedWith(compareBy { it.date })

    /**
     * Update the recyclerview list
     */
    private fun replaceItems(items: List<Todo>) {
        if (items.isNotEmpty()) {
            binding.layoutNoData.hide()
            binding.recyclerviewTaskList.show()
            taskAdapter?.submitList(items)
            binding.recyclerviewTaskList.smoothScrollToPosition(0)
        } else {
            binding.recyclerviewTaskList.hide()
            binding.layoutNoData.show()
        }

    }

    override fun onFilterItemClick(item: String) {
        if (item == "All") {
            replaceItems(todosOriginal)
        } else {
            replaceItems(getListByCategory(item))
        }
    }

    override fun onSortItemClick(item: String) {
        when (item) {
            "Priority - High to low" -> {
                replaceItems(getSortedListByPriorityHighLow())
            }

            "Priority - Low to high" -> {
                replaceItems(getSortedListByPriorityLowHigh())
            }

            "Date" -> {
                replaceItems(getSortedListByDate())
            }
        }
    }
}