package com.cns.mytaskmanager.ui.add_update_task

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.databinding.FragmentAddUpdateTaskBinding
import com.cns.mytaskmanager.utils.hide
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.show
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddUpdateTaskFragment : Fragment() {
    private val addUpdateTaskViewModel: AddUpdateTaskViewModel by viewModels()
    private lateinit var binding: FragmentAddUpdateTaskBinding
    private val args by navArgs<AddUpdateTaskFragmentArgs>()

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUpdateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupClickListeners()
    }

    private fun initView() {
        if (args.taskItem != null) {
            binding.toolbarTitle.text = "Update task"
            binding.btnSubmit.text = "Update"
            binding.checkboxStatus.show()
            binding.etTitle.setText(args.taskItem!!.title)
            binding.etNote.setText(args.taskItem!!.todo)
            binding.etDate.setText(args.taskItem!!.date)
            binding.autoCompleteCategory.setText(args.taskItem!!.category)
            binding.autoCompletePriority.setText(args.taskItem!!.priority)
        } else {
            binding.toolbarTitle.text = "Add task"
            binding.btnSubmit.text = "Submit"
            binding.checkboxStatus.hide()
        }

        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapterCategory =
            context?.let { ArrayAdapter(it, R.layout.drop_down_item, categories) }
        binding.autoCompleteCategory.setAdapter(arrayAdapterCategory)

        val priorities = resources.getStringArray(R.array.priorities)
        val arrayAdapterPriority =
            context?.let { ArrayAdapter(it, R.layout.drop_down_item, priorities) }
        binding.autoCompletePriority.setAdapter(arrayAdapterPriority)

        binding.etDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.btnCancel.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.btnSubmit.setCustomClickListener {
            validateData()
        }
    }

    private fun validateData() {

    }

    private fun showDatePicker() {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it, { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding.etDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog?.show()
    }
}