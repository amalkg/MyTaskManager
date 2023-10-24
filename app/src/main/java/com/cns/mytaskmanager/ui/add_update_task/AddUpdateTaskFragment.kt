package com.cns.mytaskmanager.ui.add_update_task

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.core.BaseFragment
import com.cns.mytaskmanager.databinding.FragmentAddUpdateTaskBinding
import com.cns.mytaskmanager.utils.convertDateToMilliseconds
import com.cns.mytaskmanager.utils.hide
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.jsonToList
import com.cns.mytaskmanager.utils.notification.NotificationWorker
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.show
import com.cns.mytaskmanager.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class AddUpdateTaskFragment : BaseFragment<FragmentAddUpdateTaskBinding, AddUpdateTaskViewModel>() {
    private val args by navArgs<AddUpdateTaskFragmentArgs>()

    private val calendar = Calendar.getInstance()

    var categoryListFromDb: ArrayList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupObservables()
        setupClickListeners()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddUpdateTaskBinding {
        return FragmentAddUpdateTaskBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): Class<AddUpdateTaskViewModel> {
        return AddUpdateTaskViewModel::class.java
    }

    override fun setupViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupObservables() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            println("categoryList in add ==$it")
            categoryListFromDb = jsonToList(it.toString())
            println("categoryListFromDb in add ==$categoryListFromDb")

        }
        viewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                if (args.todoItem != null) {
                    updateTodoItem()
                } else {
                    submitTodoItem()
                }

            } else {
                requireContext().showToast(getString(R.string.please_update_all_fields))
            }
        }
    }

    private fun initView() {
        if (args.todoItem != null) {
            binding.toolbarTitle.text = getString(R.string.update_task)
            binding.btnSubmit.text = getString(R.string.update)
            binding.btnCancel.text = getString(R.string.delete)
            binding.checkboxStatus.show()
            viewModel.titleLiveData.value = args.todoItem!!.title
            viewModel.noteLiveData.value = args.todoItem!!.todo
            viewModel.dateLiveData.value = args.todoItem!!.date
            viewModel.categoryLiveData.value = args.todoItem!!.category
            viewModel.priorityLiveData.value = args.todoItem!!.priority
            binding.checkboxStatus.isChecked = args.todoItem!!.completed
        } else {
            binding.toolbarTitle.text = getString(R.string.add_task)
            binding.btnSubmit.text = getString(R.string.add_task)
            binding.btnCancel.text = getString(R.string.cancel)
            binding.checkboxStatus.hide()
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.etCategory.setCustomClickListener {
            hideKeyboard()
            showCategoryMenu()
        }
        binding.etPriority.setCustomClickListener {
            hideKeyboard()
            showPriorityMenu()
        }
        binding.etDate.setCustomClickListener {
            hideKeyboard()
            showDatePicker()
        }
        binding.btnCancel.setCustomClickListener {
            if (args.todoItem != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_task))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_delete_the_task))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        viewModel.removeTodo(args.position)
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                hideKeyboard()
                findNavController().navigateUp()
            }
        }
        binding.btnSubmit.setCustomClickListener {
            viewModel.validateForm()
        }
    }

    private fun showPriorityMenu() {
        val popup = PopupMenu(requireContext(), binding.etPriority)

        val dynamicMenuItems = resources.getStringArray(R.array.priorities)

        for (item in dynamicMenuItems) {
            popup.menu.add(item)
        }

        popup.setOnMenuItemClickListener { item ->
            binding.etPriority.setText(item.title)
            false
        }

        popup.show()
    }

    private fun showCategoryMenu() {
        val popup = PopupMenu(requireContext(), binding.etCategory)

        for (item in categoryListFromDb.distinct()) {
            popup.menu.add(item)
        }

        popup.setOnMenuItemClickListener { item ->
            binding.etCategory.setText(item.title)
            false
        }

        popup.show()
    }

    private fun updateTodoItem() {
        val id = args.todoItem?.id
        val completed = binding.checkboxStatus.isChecked

        viewModel.updateTodo(
            args.position,
            Todo.newBuilder()
                .setId(id!!)
                .setTitle(viewModel.titleLiveData.value)
                .setCategory(viewModel.categoryLiveData.value)
                .setTodo(viewModel.noteLiveData.value)
                .setCompleted(completed)
                .setUserId(1)
                .setDate(viewModel.dateLiveData.value)
                .setPriority(viewModel.priorityLiveData.value)
                .build()
        )
        if (completed) {
            WorkManager.getInstance(requireContext()).cancelAllWorkByTag(id.toString())
        } else {
            addTaskNotification(id)
        }
        hideKeyboard()
        findNavController().navigateUp()
        requireContext().showToast(getString(R.string.task_updated_successfully))
    }

    private fun submitTodoItem() {
        val randomInt = (100..100000).random()
        viewModel.addTodo(
            Todo.newBuilder()
                .setId(randomInt)
                .setTitle(viewModel.titleLiveData.value)
                .setCategory(viewModel.categoryLiveData.value)
                .setTodo(viewModel.noteLiveData.value)
                .setCompleted(false)
                .setUserId(1)
                .setDate(viewModel.dateLiveData.value)
                .setPriority(viewModel.priorityLiveData.value)
                .build()
        )
        addTaskNotification(randomInt)
        hideKeyboard()
        findNavController().navigateUp()
        requireContext().showToast(getString(R.string.task_added_successfully))
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

    private fun addTaskNotification(randomInt: Int) {
        val data =
            Data.Builder().putString("notification_title", viewModel.titleLiveData.value)
                .putString("notification_description", viewModel.noteLiveData.value)
                .build()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)

        val dateString = binding.etDate.text.toString() + " 09:00:00"

        val currentMilliseconds = current.convertDateToMilliseconds()
        val futureMilliseconds = dateString.convertDateToMilliseconds()

        val minuteDuration =
            TimeUnit.MILLISECONDS.toMinutes(futureMilliseconds - currentMilliseconds)

        val workRequest = if (futureMilliseconds > currentMilliseconds) {
            OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(minuteDuration, TimeUnit.MINUTES)
                .addTag(randomInt.toString())
                .setInputData(data).build()
        } else {
            OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(30, TimeUnit.MINUTES)
                .addTag(randomInt.toString())
                .setInputData(data).build()
        }

        WorkManager.getInstance(requireContext())
            .beginUniqueWork("notification_work", ExistingWorkPolicy.APPEND, workRequest).enqueue()
    }
}