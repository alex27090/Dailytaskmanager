package com.dailytask.manager.ui.category

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dailytask.manager.databinding.FragmentAddCategoryBinding
import com.dailytask.manager.ui.viewmodel.CategoryViewModel
import com.dailytask.manager.ui.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AddCategoryFragment : Fragment() {

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoryViewModel

    // Pastel preset colors to pick from
    private val presetColors = listOf(
        "#FFB3C6", "#B5D5FF", "#B5F0C0",
        "#FFE5B3", "#E5B3FF", "#FFB3B3",
        "#B3F0F0", "#FFD5B3", "#C8C8FF"
    )
    private var selectedColor = presetColors[0]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val factory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)

        setupColorPicker()
        setupSaveButton()
    }

    private fun setupColorPicker() {
        val colorViews = listOf(
            binding.color1, binding.color2, binding.color3,
            binding.color4, binding.color5, binding.color6,
            binding.color7, binding.color8, binding.color9
        )

        colorViews.forEachIndexed { index, imageView ->
            val hex = presetColors[index]
            imageView.setBackgroundColor(Color.parseColor(hex))
            imageView.setOnClickListener {
                selectedColor = hex
                colorViews.forEach { it.scaleX = 1f; it.scaleY = 1f }
                imageView.scaleX = 1.3f
                imageView.scaleY = 1.3f
                binding.previewCard.setCardBackgroundColor(Color.parseColor(hex))
            }
        }

        // Select first by default
        colorViews[0].scaleX = 1.3f
        colorViews[0].scaleY = 1.3f
        binding.previewCard.setCardBackgroundColor(Color.parseColor(selectedColor))
    }

    private fun setupSaveButton() {
        binding.btnSaveCategory.setOnClickListener {
            val name = binding.etCategoryName.text.toString().trim()
            if (name.isBlank()) {
                Snackbar.make(binding.root, "Please enter a category name", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveCategory(
                name = name,
                colorHex = selectedColor,
                iconName = "ic_cat_default"
            )
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
