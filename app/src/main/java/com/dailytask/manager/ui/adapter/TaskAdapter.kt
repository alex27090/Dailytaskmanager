package com.dailytask.manager.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailytask.manager.R
import com.dailytask.manager.databinding.ItemTaskBinding
import com.dailytask.manager.model.Priority
import com.dailytask.manager.model.Task
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskAdapter(
    private val onTaskChecked: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    private val dateFmt = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    fun getTaskAt(position: Int): Task = getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description
            binding.cbComplete.isChecked = task.isCompleted


            binding.tvTitle.paintFlags = if (task.isCompleted)
                binding.tvTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            else
                binding.tvTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()


            task.dueDate?.let {
                binding.tvDueDate.text = it.format(dateFmt)
                binding.tvDueDate.setTextColor(
                    if (task.isOverdue) ContextCompat.getColor(binding.root.context, R.color.overdue_red)
                    else ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
            } ?: run { binding.tvDueDate.text = "" }


            binding.ivReminder.visibility =
                if (task.reminderTime != null) android.view.View.VISIBLE else android.view.View.GONE


            binding.cbComplete.setOnClickListener { onTaskChecked(task) }
            binding.root.setOnClickListener { onTaskClicked(task) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(old: Task, new: Task) = old.id == new.id
            override fun areContentsTheSame(old: Task, new: Task) = old == new
        }
    }
}


