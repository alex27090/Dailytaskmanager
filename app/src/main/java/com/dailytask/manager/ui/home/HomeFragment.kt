package com.dailytask.manager.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dailytask.manager.R
import com.dailytask.manager.databinding.FragmentHomeBinding
import com.dailytask.manager.ui.adapter.TaskAdapter
import com.dailytask.manager.ui.task.AddEditTaskActivity
import com.dailytask.manager.ui.viewmodel.HomeViewModel
import com.dailytask.manager.ui.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var taskAdapter: TaskAdapter

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        

        val factory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        setupRecyclerView()
        setupFab()
        setupDateSelector()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskChecked = { task -> viewModel.toggleTaskComplete(task) },
            onTaskClicked = { task ->
                val intent = Intent(requireContext(), AddEditTaskActivity::class.java).apply {
                    putExtra("taskId", task.id)
                }
                startActivity(intent)
            }
        )

        binding.recyclerTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Swipe-to-delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = taskAdapter.getTaskAt(viewHolder.adapterPosition)
                viewModel.deleteTask(task)
                Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { viewModel.saveDeletedTask(task) }
                    .show()
            }
        }).attachToRecyclerView(binding.recyclerTasks)
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            val intent = Intent(requireContext(), AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDateSelector() {
        binding.btnPrevDay.setOnClickListener {
            viewModel.selectedDate.value?.let {
                viewModel.selectDate(it.minusDays(1))
            }
        }
        binding.btnNextDay.setOnClickListener {
            viewModel.selectedDate.value?.let {
                viewModel.selectDate(it.plusDays(1))
            }
        }
        binding.btnToday.setOnClickListener {
            viewModel.selectDate(LocalDate.now())
        }
    }

    private fun observeViewModel() {
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            binding.tvCurrentDate.text = date.format(dateFormatter)
            binding.btnToday.isVisible = !date.isEqual(LocalDate.now())
        }

        viewModel.tasksForSelectedDay.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            binding.emptyState.isVisible = tasks.isEmpty()
            binding.recyclerTasks.isVisible = tasks.isNotEmpty()
        }

        viewModel.dailySummary.observe(viewLifecycleOwner) { summary ->
            binding.tvTaskSummary.text = "${summary.completedTasks}/${summary.totalTasks} tasks done"
            binding.progressCompletion.progress = (summary.completionRate * 100).toInt()
            binding.tvOverdue.isVisible = summary.overdueTasks > 0
            binding.tvOverdue.text = "${summary.overdueTasks} overdue"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
