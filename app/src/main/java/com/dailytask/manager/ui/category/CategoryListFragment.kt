package com.dailytask.manager.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dailytask.manager.R
import com.dailytask.manager.databinding.FragmentCategoryListBinding
import com.dailytask.manager.ui.adapter.CategoryAdapter
import com.dailytask.manager.ui.viewmodel.CategoryViewModel
import com.dailytask.manager.ui.viewmodel.ViewModelFactory

class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)

        categoryAdapter = CategoryAdapter(
            onDeleteClicked = { category -> viewModel.deleteCategory(category) }
        )

        binding.recyclerCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabAddCategory.setOnClickListener {
            findNavController().navigate(R.id.action_categories_to_addCategory)
        }

        viewModel.categoriesWithTasks.observe(viewLifecycleOwner) { categoriesWithTasks ->
            categoryAdapter.submitList(categoriesWithTasks)
            binding.emptyState.visibility =
                if (categoriesWithTasks.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
