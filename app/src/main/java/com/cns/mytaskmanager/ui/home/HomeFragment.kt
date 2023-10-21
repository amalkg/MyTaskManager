package com.cns.mytaskmanager.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cns.mytaskmanager.MainActivity
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.data.model.Todos
import com.cns.mytaskmanager.databinding.FragmentHomeBinding
import com.cns.mytaskmanager.utils.PriorityComparatorHighLow
import com.cns.mytaskmanager.utils.PriorityComparatorLowHigh
import com.cns.mytaskmanager.utils.bottom_sheet.BottomSheetFilterListDialogFragment
import com.cns.mytaskmanager.utils.bottom_sheet.BottomSheetSortListDialogFragment
import com.cns.mytaskmanager.utils.safeNavigate
import com.cns.mytaskmanager.utils.setCustomClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), BottomSheetFilterListDialogFragment.OnItemClickListener,
    BottomSheetSortListDialogFragment.OnItemClickListener {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    private var taskAdapter: TaskAdapter? = null

    private var todosOriginal: ArrayList<Todo> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservables()
        setupClickListeners()
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
        homeViewModel.todoList.observe(viewLifecycleOwner) { list ->
            todosOriginal = arrayListOf()
            todosOriginal.addAll(list)
            if (list.isNullOrEmpty()) {
//                binding.constraintLayout3.hide()
//                binding.lytNoItems.show()
                homeViewModel.fetchTaskList()
            } else {
//                binding.lytNoItems.hide()
//                binding.constraintLayout3.show()
                taskAdapter?.submitList(list)
                taskAdapter?.notifyDataSetChanged()
            }
        }

        homeViewModel.todoListFromApi.observe(viewLifecycleOwner) {

        }
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
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                v: RecyclerView,
                h: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(h: RecyclerView.ViewHolder, dir: Int) =
                removeAt(h.adapterPosition)
        }).attachToRecyclerView(binding.recyclerviewTaskList)
    }

    fun removeAt(index: Int) {
        todosOriginal.removeAt(index)
        homeViewModel.removeTodo(index)
        taskAdapter?.notifyItemRemoved(index)
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

    private fun getListByCategory(category: String): List<Todo> =
        todosOriginal.filter { it.category == category }

    private fun getListByStatus(status: Boolean): List<Todo> =
        todosOriginal.filter { it.completed == status }

    private fun getSortedListByPriorityHighLow(): List<Todo> =
        todosOriginal.sortedWith(PriorityComparatorHighLow())

    private fun getSortedListByPriorityLowHigh(): List<Todo> =
        todosOriginal.sortedWith(PriorityComparatorLowHigh())

    private fun getSortedListByDate(): List<Todo> = todosOriginal.sortedWith(compareBy { it.date })

    private fun replaceItems(items: List<Todo>) {
        taskAdapter?.submitList(items)
        binding.recyclerviewTaskList.smoothScrollToPosition(0)
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