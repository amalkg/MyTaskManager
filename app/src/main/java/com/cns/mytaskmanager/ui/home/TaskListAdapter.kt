package com.cns.mytaskmanager.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cns.mytaskmanager.data.model.TaskListResponse
import com.cns.mytaskmanager.databinding.ItemTaskBinding
import javax.inject.Inject

class TaskListAdapter @Inject constructor() : RecyclerView.Adapter<TaskViewHolder>() {

    private var todoList = mutableListOf<TaskListResponse.Todos>()

    fun setTaskList(tasks: List<TaskListResponse.Todos>) {
        this.todoList = tasks.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = todoList[position]
        holder.binding.tvTitle.text = task.title
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}

class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

}