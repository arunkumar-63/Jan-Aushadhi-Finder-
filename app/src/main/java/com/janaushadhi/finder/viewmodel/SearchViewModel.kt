package com.janaushadhi.finder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janaushadhi.finder.data.MedicineEntity
import com.janaushadhi.finder.repository.MedicineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val repository: MedicineRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(CATEGORY_ALL)
    val suggestion = MutableStateFlow<String?>(null)

    val categories = listOf(
        CATEGORY_ALL,
        "Fever & Pain",
        "Diabetes",
        "BP & Heart",
        "Gastric",
        "Allergy & Respiratory",
        "Antibiotic",
        "Vitamins & Supplements"
    )

    val medicines: StateFlow<List<MedicineEntity>> = combine(
        query.debounce(180).distinctUntilChanged(),
        selectedCategory
    ) { currentQuery, category -> currentQuery to category }
        .debounce(180)
        .distinctUntilChanged()
        .flatMapLatest { (currentQuery, category) -> repository.observeSearch(currentQuery, category) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(value: String) {
        query.value = value
        viewModelScope.launch {
            suggestion.value = repository.didYouMean(value)
        }
    }

    fun updateCategory(value: String) {
        selectedCategory.value = value
    }

    fun addToSavings(medicine: MedicineEntity) {
        repository.addToSavings(medicine)
    }

    private companion object {
        const val CATEGORY_ALL = "All"
    }
}
