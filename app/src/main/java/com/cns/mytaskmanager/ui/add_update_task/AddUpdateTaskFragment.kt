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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
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
class AddUpdateTaskFragment : Fragment() {
    private val addUpdateTaskViewModel: AddUpdateTaskViewModel by viewModels()
    private lateinit var binding: FragmentAddUpdateTaskBinding
    private val args by navArgs<AddUpdateTaskFragmentArgs>()

    private val calendar = Calendar.getInstance()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUpdateTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = addUpdateTaskViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        createNotificationChannel()
        setupObservables()
        setupClickListeners()
    }

    private fun setupObservables() {
        addUpdateTaskViewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
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
            addUpdateTaskViewModel.titleLiveData.value = args.todoItem!!.title
            addUpdateTaskViewModel.noteLiveData.value = args.todoItem!!.todo
            addUpdateTaskViewModel.dateLiveData.value = args.todoItem!!.date
            addUpdateTaskViewModel.categoryLiveData.value = args.todoItem!!.category
            addUpdateTaskViewModel.priorityLiveData.value = args.todoItem!!.priority
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
                addUpdateTaskViewModel.removeTodo(args.position)
            }
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.btnSubmit.setCustomClickListener {
            addUpdateTaskViewModel.validateForm()
        }
    }

    private fun updateTodoItem() {
        val id = args.todoItem?.id
        val completed = binding.checkboxStatus.isChecked

        addUpdateTaskViewModel.updateTodo(
            args.position,
            Todo.newBuilder()
                .setId(id!!)
                .setTitle(addUpdateTaskViewModel.titleLiveData.value)
                .setCategory(addUpdateTaskViewModel.categoryLiveData.value)
                .setTodo(addUpdateTaskViewModel.noteLiveData.value)
                .setCompleted(completed)
                .setUserId(1)
                .setDate(addUpdateTaskViewModel.dateLiveData.value)
                .setPriority(addUpdateTaskViewModel.priorityLiveData.value)
                .build()
        )
        hideKeyboard()
        findNavController().navigateUp()
    }

    private fun submitTodoItem() {
        val randomInt = (100..100000).random()
        addUpdateTaskViewModel.addTodo(
            Todo.newBuilder()
                .setId(randomInt)
                .setTitle(addUpdateTaskViewModel.titleLiveData.value)
                .setCategory(addUpdateTaskViewModel.categoryLiveData.value)
                .setTodo(addUpdateTaskViewModel.noteLiveData.value)
                .setCompleted(false)
                .setUserId(1)
                .setDate(addUpdateTaskViewModel.dateLiveData.value)
                .setPriority(addUpdateTaskViewModel.priorityLiveData.value)
                .build()
        )
        hideKeyboard()
        findNavController().navigateUp()
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