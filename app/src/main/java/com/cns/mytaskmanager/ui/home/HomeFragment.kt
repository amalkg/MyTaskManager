package com.cns.mytaskmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.data.model.Todos
import com.cns.mytaskmanager.databinding.FragmentHomeBinding
import com.cns.mytaskmanager.utils.FilterByStatusType
import com.cns.mytaskmanager.utils.safeNavigate
import com.cns.mytaskmanager.utils.setCustomClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    private var taskAdapter: TaskAdapter? = null

    private var todosOriginal: ArrayList<Todos> = ArrayList()

    private var fabVisible = false

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
        homeViewModel.fetchTaskList()
    }

    private fun setupClickListeners() {
        binding.idFABEdit.setCustomClickListener {
            if (!fabVisible) {
                binding.idFABAdd.show()
                binding.idFABFilter.show()
                binding.idFABSort.show()

                binding.idFABEdit.setImageDrawable(context?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1, R.drawable.ic_close
                    )
                })

                fabVisible = true
            } else {
                binding.idFABAdd.hide()
                binding.idFABFilter.hide()
                binding.idFABSort.hide()

                binding.idFABEdit.setImageDrawable(context?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1, R.drawable.ic_edit
                    )
                })

                fabVisible = false
            }
        }


        binding.idFABAdd.setCustomClickListener {
            safeNavigate(
                HomeFragmentDirections
                    .actionHomeFragmentToAddUpdateTaskFragment(
                        null
                    )
            )
        }

        binding.idFABFilter.setCustomClickListener {

        }

        binding.imageThreeDot.setCustomClickListener {
            showPopUpMenu()
        }
    }

    private fun showPopUpMenu() {
        val popupMenu: PopupMenu? = context?.let { PopupMenu(it, binding.imageThreeDot) }
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_all ->
                    filterByStatus(FilterByStatusType.ALL)

                R.id.action_completed ->
                    filterByStatus(FilterByStatusType.COMPLETED)

                R.id.action_pending ->
                    filterByStatus(FilterByStatusType.PENDING)
            }
            true
        }
        popupMenu?.show()
    }

    private fun setupObservables() {
        homeViewModel.taskList.observe(viewLifecycleOwner) { list ->
            todosOriginal = arrayListOf()
            todosOriginal.addAll(list)
            if (list.isNullOrEmpty()) {
//                binding.constraintLayout3.hide()
//                binding.lytNoItems.show()
            } else {
//                binding.lytNoItems.hide()
//                binding.constraintLayout3.show()
                taskAdapter?.submitList(list)
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter { item ->
            onClickTask(item)
        }
        binding.recyclerview.apply {
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = taskAdapter
        }
    }

    private fun onClickTask(item: Todos) {
        safeNavigate(
            HomeFragmentDirections
                .actionHomeFragmentToAddUpdateTaskFragment(
                    item
                )
        )
    }

    private fun getCompletedList(): List<Todos> = todosOriginal.filter { it.completed }

    private fun getPendingList(): List<Todos> = todosOriginal.filter { !it.completed }

    private fun filterByStatus(filterByStatusType: FilterByStatusType) {
        when (filterByStatusType) {
            FilterByStatusType.ALL -> replaceSortedItems(todosOriginal)
            FilterByStatusType.COMPLETED -> replaceSortedItems(getCompletedList())
            FilterByStatusType.PENDING -> replaceSortedItems(getPendingList())
        }
    }

    private fun replaceSortedItems(items: List<Todos>) {
        taskAdapter?.submitList(items)
    }
}