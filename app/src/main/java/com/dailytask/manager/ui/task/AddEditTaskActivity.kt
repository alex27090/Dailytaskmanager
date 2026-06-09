package com.dailytask.manager.ui.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.dailytask.manager.databinding.ActivityAddEditTaskBinding
import com.dailytask.manager.model.Category
import com.dailytask.manager.model.Priority
import com.dailytask.manager.ui.viewmodel.TaskUiState
import com.dailytask.manager.ui.viewmodel.TaskViewModel
import com.dailytask.manager.ui.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var viewModel: TaskViewModel
    private var taskId: Long = 0L

    private var selectedDueDate: LocalDate? = null
    private var selectedReminder: LocalDateTime? = null
    private var selectedCategory: Category? = null
    private var allCategories: List<Category> = emptyList()

    private val dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    private val timeFmt = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        taskId = intent.getLongExtra("taskId", 0L)
        
        val factory = ViewModelFactory(this, taskId)
        viewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (taskId == 0L) "New Task" else "Edit Task"
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupPriorityChips()
        setupDateTimePickers()
        setupSaveButton()
        setupDeleteButton()
        observeViewModel()
        setupCategorySpinner()
    }

    private fun setupDeleteButton() {
        binding.btnDelete.isVisible = taskId != 0L
        binding.btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteTask() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupPriorityChips() {
        binding.chipMedium.isChecked = true
    }

    private fun setupCategorySpinner() {
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (allCategories.isNotEmpty() || position == 0) {
                    selectedCategory = if (position == 0) null else allCategories[position - 1]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDateTimePickers() {
        binding.btnPickDueDate.setOnClickListener {
            val now = LocalDate.now()
            DatePickerDialog(this, { _, y, m, d ->
                selectedDueDate = LocalDate.of(y, m + 1, d)
                binding.tvDueDate.text = selectedDueDate!!.format(dateFmt)
                binding.btnClearDate.isVisible = true
            }, now.year, now.monthValue - 1, now.dayOfMonth).show()
        }

        binding.btnClearDate.setOnClickListener {
            selectedDueDate = null
            binding.tvDueDate.text = "No due date"
            binding.btnClearDate.isVisible = false
        }

        binding.btnPickReminder.setOnClickListener {
            val base = selectedDueDate ?: LocalDate.now()
            DatePickerDialog(this, { _, y, m, d ->
                val now = java.util.Calendar.getInstance()
                TimePickerDialog(this, { _, hour, minute ->
                    selectedReminder = LocalDateTime.of(y, m + 1, d, hour, minute)
                    binding.tvReminder.text = selectedReminder!!.format(timeFmt)
                    binding.btnClearReminder.isVisible = true
                }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), false).show()
            }, base.year, base.monthValue - 1, base.dayOfMonth).show()
        }

        binding.btnClearReminder.setOnClickListener {
            selectedReminder = null
            binding.tvReminder.text = "No reminder"
            binding.btnClearReminder.isVisible = false
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val priority = when {
                binding.chipHigh.isChecked -> Priority.HIGH
                binding.chipLow.isChecked -> Priority.LOW
                else -> Priority.MEDIUM
            }
            viewModel.saveTask(
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                priority = priority,
                category = selectedCategory,
                dueDate = selectedDueDate,
                reminderTime = selectedReminder
            )
        }
    }

    private fun updateSpinnerSelection() {
        selectedCategory?.let { current ->
            val index = allCategories.indexOfFirst { it.id == current.id }
            if (index != -1) {
                binding.spinnerCategory.setSelection(index + 1, false)
            }
        } ?: run {
            binding.spinnerCategory.setSelection(0, false)
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this) { cats ->
            allCategories = cats
            val names = listOf("No category") + cats.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
            updateSpinnerSelection()
        }

        viewModel.editingTask.observe(this) { task ->
            task ?: return@observe
            binding.etTitle.setText(task.title)
            binding.etDescription.setText(task.description)
            selectedDueDate = task.dueDate
            selectedReminder = task.reminderTime
            selectedCategory = task.category
            
            updateSpinnerSelection()

            when (task.priority) {
                Priority.HIGH -> binding.chipHigh.isChecked = true
                Priority.LOW -> binding.chipLow.isChecked = true
                else -> binding.chipMedium.isChecked = true
            }
            task.dueDate?.let { 
                binding.tvDueDate.text = it.format(dateFmt) 
                binding.btnClearDate.isVisible = true
            }
            task.reminderTime?.let { 
                binding.tvReminder.text = it.format(timeFmt) 
                binding.btnClearReminder.isVisible = true
            }
        }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is TaskUiState.Loading -> binding.btnSave.isEnabled = false
                is TaskUiState.Saved -> finish()
                is TaskUiState.Error -> {
                    binding.btnSave.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                else -> binding.btnSave.isEnabled = true
            }
        }
    }
}
