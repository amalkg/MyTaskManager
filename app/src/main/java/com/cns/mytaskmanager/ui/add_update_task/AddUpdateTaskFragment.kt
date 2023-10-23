package com.cns.mytaskmanager.ui.add_update_task

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.core.BaseFragment
import com.cns.mytaskmanager.databinding.FragmentAddUpdateTaskBinding
import com.cns.mytaskmanager.utils.convertDateToMilliseconds
import com.cns.mytaskmanager.utils.hide
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.notification.AlarmReceiver
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.show
import com.cns.mytaskmanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddUpdateTaskFragment : BaseFragment<FragmentAddUpdateTaskBinding, AddUpdateTaskViewModel>() {
    private val args by navArgs<AddUpdateTaskFragmentArgs>()

    private val calendar = Calendar.getInstance()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        createNotificationChannel()
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
        viewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                if (args.todoItem != null) {
                    updateTodoItem()
                } else {
                    submitTodoItem()
                }

                alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                pendingIntent =
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                val dateString = binding.etDate.text.toString() + " 09:00:00"
                val inputFormat = "dd-MM-yyyy HH:mm:ss"
                val milliseconds = dateString.convertDateToMilliseconds(inputFormat)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, milliseconds,
                    AlarmManager.INTERVAL_DAY, pendingIntent
                )
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

        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapterCategory =
            context?.let { ArrayAdapter(it, R.layout.drop_down_item, categories) }
        binding.autoCompleteCategory.setAdapter(arrayAdapterCategory)

        val priorities = resources.getStringArray(R.array.priorities)
        val arrayAdapterPriority =
            context?.let { ArrayAdapter(it, R.layout.drop_down_item, priorities) }
        binding.autoCompletePriority.setAdapter(arrayAdapterPriority)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "task-manager-notification-channel"
            val description = "Channel for Alarm manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("task-manager-notification", name, importance)
            channel.description = description
            val notificationManager = context?.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.autoCompleteCategory.setCustomClickListener {
            hideKeyboard()
        }
        binding.autoCompletePriority.setCustomClickListener {
            hideKeyboard()
        }
        binding.etDate.setCustomClickListener {
            hideKeyboard()
            showDatePicker()
        }
        binding.btnCancel.setCustomClickListener {
            if (args.todoItem != null) {
                viewModel.removeTodo(args.position)
            }
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.btnSubmit.setCustomClickListener {
            viewModel.validateForm()
        }
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
}