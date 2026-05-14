package com.janaushadhi.finder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.janaushadhi.finder.JanAushadhiApp
import com.janaushadhi.finder.R
import com.janaushadhi.finder.databinding.FragmentHomeBinding
import com.janaushadhi.finder.ui.calculator.SelectedMedicineAdapter
import com.janaushadhi.finder.viewmodel.HomeViewModel
import com.janaushadhi.finder.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory((requireActivity().application as JanAushadhiApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = SelectedMedicineAdapter()
        binding.selectedMedicineRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.selectedMedicineRecycler.adapter = adapter
        binding.languageButton.setOnClickListener { showLanguagePicker() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    updateDateTime()
                    while (true) {
                        delay(60_000)
                        updateDateTime()
                    }
                }
                launch {
                    viewModel.selectedMedicines.collect { medicines ->
                        adapter.submitList(medicines)
                        val monthly = medicines.sumOf { it.savings }
                        binding.monthlySavingsText.text = "₹${String.format("%.0f", monthly)}"
                        binding.yearlySavingsText.text = "₹${String.format("%.0f", monthly * 12)} projected yearly savings"
                        binding.selectedCountText.text = if (medicines.isEmpty()) {
                            "No medicines selected yet"
                        } else {
                            "${medicines.size} medicine${if (medicines.size == 1) "" else "s"} selected"
                        }
                        binding.emptySelectedCard.visibility = if (medicines.isEmpty()) View.VISIBLE else View.GONE
                        binding.selectedMedicineRecycler.visibility = if (medicines.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    private fun updateDateTime() {
        val formatted = SimpleDateFormat("EEE, dd MMM yyyy | hh:mm a", Locale.getDefault()).format(Date())
        binding.dateTimeText.text = formatted
    }

    private fun showLanguagePicker() {
        val languages = listOf(
            LanguageOption(getString(R.string.language_english), ""),
            LanguageOption(getString(R.string.language_hindi), "hi"),
            LanguageOption(getString(R.string.language_telugu), "te"),
            LanguageOption(getString(R.string.language_tamil), "ta"),
            LanguageOption(getString(R.string.language_kannada), "kn"),
            LanguageOption(getString(R.string.language_marathi), "mr")
        )
        val currentTag = requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .getString("language_tag", "")
            .orEmpty()
        val checkedIndex = languages.indexOfFirst { it.tag == currentTag }.coerceAtLeast(0)

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages.map { it.label }.toTypedArray(), checkedIndex) { dialog, which ->
                val selected = languages[which]
                requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("language_tag", selected.tag)
                    .apply()
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selected.tag))
                dialog.dismiss()
            }
            .show()
    }

    private data class LanguageOption(
        val label: String,
        val tag: String
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
