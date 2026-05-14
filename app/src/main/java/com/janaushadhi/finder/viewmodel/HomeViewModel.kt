package com.janaushadhi.finder.viewmodel

import androidx.lifecycle.ViewModel
import com.janaushadhi.finder.data.MedicineEntity
import com.janaushadhi.finder.repository.MedicineRepository
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(repository: MedicineRepository) : ViewModel() {
    val selectedMedicines: StateFlow<List<MedicineEntity>> = repository.observeSelectedMedicines()
}
