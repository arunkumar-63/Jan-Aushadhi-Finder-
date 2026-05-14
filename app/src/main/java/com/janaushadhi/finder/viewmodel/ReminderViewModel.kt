package com.janaushadhi.finder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janaushadhi.finder.data.ReminderEntity
import com.janaushadhi.finder.repository.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: MedicineRepository
) : ViewModel() {
    val reminders: StateFlow<List<ReminderEntity>> = repository.observeReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addReminder(medicineName: String, reminderTime: Long, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            onSaved(repository.addReminder(medicineName, reminderTime))
        }
    }
}
