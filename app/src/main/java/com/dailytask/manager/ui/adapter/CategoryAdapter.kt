package com.dailytask.manager.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailytask.manager.R
import com.dailytask.manager.databinding.ItemCategoryBinding
import com.dailytask.manager.model.Category
import com.dailytask.manager.model.Task

class CategoryAdapter(
    private val onDeleteClicked: (Category) -> Unit
) : ListAdapter<Pair<Category, List<Task>>, CategoryAdapter.CategoryViewHolder>(DIFF_CALLBACK) {

    // Keep track of which categories are expanded (College Student Style)
    private val expandedIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<Category, List<Task>>) {
            val (category, tasks) = pair
            binding.tvCategoryName.text = category.name
            binding.viewColorDot.setBackgroundColor(Color.parseColor(category.colorHex))
            binding.btnDelete.setOnClickListener { onDeleteClicked(category) }


            val isExpanded = expandedIds.contains(category.id)
            binding.tvTasksList.visibility = if (isExpanded) View.VISIBLE else View.GONE
            

            binding.ivExpand.rotation = if (isExpanded) 90f else 0f
            binding.ivExpand.visibility = View.VISIBLE


            if (tasks.isNotEmpty()) {
                val taskNames = tasks.joinToString("\n") { "• ${it.title}" }
                binding.tvTasksList.text = taskNames
            } else {
                binding.tvTasksList.text = "No pending tasks"
            }


            binding.root.setOnClickListener {
                if (expandedIds.contains(category.id)) {
                    expandedIds.remove(category.id)
                } else {
                    expandedIds.add(category.id)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pair<Category, List<Task>>>() {
            override fun areItemsTheSame(old: Pair<Category, List<Task>>, new: Pair<Category, List<Task>>) = 
                old.first.id == new.first.id
            override fun areContentsTheSame(old: Pair<Category, List<Task>>, new: Pair<Category, List<Task>>) = 
                old == new
        }
    }
}
