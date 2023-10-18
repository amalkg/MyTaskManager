package com.cns.mytaskmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cns.mytaskmanager.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var taskListAdapter: TaskListAdapter

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
        binding.recyclerview.adapter = taskListAdapter

        homeViewModel.taskList.observe(viewLifecycleOwner) {
            taskListAdapter.setTaskList(it)
        }

        homeViewModel.progressBarStatus.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

        homeViewModel.fetchTaskList()
    }
}