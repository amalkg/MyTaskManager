package com.cns.mytaskmanager.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.Todo
import com.cns.mytaskmanager.databinding.ItemTaskBinding
import com.cns.mytaskmanager.utils.setCustomClickListener

class TaskAdapter(val onClickTask: (Todo) -> Unit) :
    ListAdapter<Todo, TaskAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Todo?>() {
        override fun areItemsTheSame(
            oldItem: Todo,
            newItem: Todo
        ): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(
            oldItem: Todo,
            newItem: Todo
        ): Boolean {
            return newItem == oldItem
        }
    }) {
    class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.also { binding ->
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.date
            binding.tvStatus.text =
                String.format(" %s", if (item.completed) "Completed" else "Pending")
            binding.tvStatus.apply {
                setTextColor(
                    if (item.completed) ContextCompat.getColor(
                        context,
                        R.color.color_green
                    ) else ContextCompat.getColor(context, R.color.color_red)
                )
            }


            binding.tagPriority.apply {
                when (item.priority) {
                    "High" -> setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_red_high
                        )
                    )

                    "Medium" -> setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_yellow_medium
                        )
                    )

                    "Low" -> setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_blue_low
                        )
                    )
                }
            }

            binding.parent.setCustomClickListener {
                onClickTask(item)
            }
        }
    }
}