package com.janaushadhi.finder.ui.reminder

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.janaushadhi.finder.JanAushadhiApp
import com.janaushadhi.finder.databinding.FragmentReminderBinding
import com.janaushadhi.finder.viewmodel.ReminderViewModel
import com.janaushadhi.finder.viewmodel.ViewModelFactory
import com.janaushadhi.finder.worker.ReminderScheduler
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderFragment : Fragment() {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val viewModel: ReminderViewModel by viewModels {
        ViewModelFactory((requireActivity().application as JanAushadhiApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val adapter = ReminderAdapter()
        binding.reminderRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.reminderRecycler.adapter = adapter

        binding.addReminderButton.setOnClickListener {
            val medicine = binding.reminderMedicineEditText.text?.toString()?.trim().orEmpty()
            if (medicine.isBlank()) {
                binding.reminderInputLayout.error = "Enter medicine name"
                return@setOnClickListener
            }
            binding.reminderInputLayout.error = null

            val firstReminder = Calendar.getInstance().apply {
                add(Calendar.MONTH, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            viewModel.addReminder(medicine, firstReminder) { id ->
                ReminderScheduler.scheduleMonthly(requireContext(), id.toInt(), medicine, firstReminder)
                binding.reminderMedicineEditText.text?.clear()
                Snackbar.make(binding.root, "Monthly refill reminder added", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reminders.collect { reminders ->
                    adapter.submitList(reminders)
                    binding.reminderEmptyCard.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
                    binding.reminderRecycler.visibility = if (reminders.isEmpty()) View.GONE else View.VISIBLE
                    binding.reminderSummaryText.text = if (reminders.isEmpty()) {
                        "No reminders yet"
                    } else {
                        "${reminders.size} active monthly reminder${if (reminders.size == 1) "" else "s"}"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
