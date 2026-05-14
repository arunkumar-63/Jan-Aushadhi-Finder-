package com.janaushadhi.finder.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.janaushadhi.finder.JanAushadhiApp
import com.janaushadhi.finder.databinding.FragmentSearchBinding
import com.janaushadhi.finder.viewmodel.SearchViewModel
import com.janaushadhi.finder.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory((requireActivity().application as JanAushadhiApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MedicineAdapter(
            onCheckAvailability = { medicine, available ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(medicine.brandName)
                    .setMessage(if (available) "Available at a nearby Jan Aushadhi Kendra" else "Currently out of stock in the sample inventory")
                    .setPositiveButton("OK", null)
                    .show()
            },
            onAddToSavings = { medicine ->
                viewModel.addToSavings(medicine)
                Snackbar.make(binding.root, "Added to savings calculator", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.medicineRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.medicineRecycler.adapter = adapter
        setupCategoryFilters()
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateQuery(text?.toString().orEmpty())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.medicines.collect { medicines ->
                        val query = binding.searchEditText.text?.toString()?.trim().orEmpty()
                        adapter.highlightBestMatch = query.length >= 3 && medicines.isNotEmpty()
                        adapter.submitList(medicines)
                        binding.searchEmptyCard.visibility = if (query.isNotBlank() && medicines.isEmpty()) View.VISIBLE else View.GONE
                        binding.medicineRecycler.visibility = if (query.isNotBlank() && medicines.isEmpty()) View.GONE else View.VISIBLE
                        binding.resultSummaryText.text = when {
                            query.isBlank() -> "Popular medicines"
                            medicines.isEmpty() -> "No matches"
                            else -> "${medicines.size} relevant matches - best match first"
                        }
                    }
                }
                launch {
                    viewModel.suggestion.collect { suggestion ->
                        if (suggestion == null) {
                            binding.suggestionText.visibility = View.GONE
                        } else {
                            binding.suggestionText.visibility = View.VISIBLE
                            binding.suggestionText.text = "Did you mean: $suggestion?"
                            binding.suggestionText.setOnClickListener {
                                binding.searchEditText.setText(suggestion)
                                binding.searchEditText.setSelection(suggestion.length)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupCategoryFilters() {
        viewModel.categories.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category
                isCheckable = true
                isChecked = index == 0
                setOnCheckedChangeListener { _, checked ->
                    if (checked) viewModel.updateCategory(category)
                }
            }
            binding.categoryChipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
