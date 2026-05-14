package com.janaushadhi.finder.repository

import com.janaushadhi.finder.data.MedicineDao
import com.janaushadhi.finder.data.MedicineEntity
import com.janaushadhi.finder.data.ReminderDao
import com.janaushadhi.finder.data.ReminderEntity
import java.text.Normalizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MedicineRepository(
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao
) {
    private val selectedMedicines = MutableStateFlow<List<MedicineEntity>>(emptyList())

    fun observeSearch(query: String, category: String = CATEGORY_ALL): Flow<List<MedicineEntity>> {
        return if (query.isBlank()) {
            medicineDao.observePopularMedicines().map { medicines -> medicines.filteredByCategory(category) }
        } else {
            val normalizedQuery = query.trim().normalized()
            medicineDao.observeAllMedicines().map { medicines ->
                fuzzySearch(medicines.filteredByCategory(category), normalizedQuery)
            }
        }
    }

    fun observeSelectedMedicines() = selectedMedicines.asStateFlow()

    fun addToSavings(medicine: MedicineEntity) {
        if (selectedMedicines.value.none { it.id == medicine.id }) {
            selectedMedicines.value = selectedMedicines.value + medicine
        }
    }

    fun observeReminders(): Flow<List<ReminderEntity>> = reminderDao.observeReminders()

    suspend fun addReminder(medicineName: String, reminderTime: Long): Long {
        return reminderDao.insert(ReminderEntity(medicineName = medicineName, reminderTime = reminderTime))
    }

    suspend fun didYouMean(query: String): String? {
        if (query.trim().length < MIN_FUZZY_QUERY_LENGTH) return null
        val normalizedQuery = query.normalized()
        return medicineDao.getAllMedicinesOnce()
            .map { medicine -> medicine to medicine.bestDistanceTo(normalizedQuery) }
            .minByOrNull { it.second }
            ?.takeIf { it.second in 1..MAX_SUGGESTION_DISTANCE }
            ?.first
            ?.brandName
    }

    private fun fuzzySearch(medicines: List<MedicineEntity>, normalizedQuery: String): List<MedicineEntity> {
        if (normalizedQuery.length < 2) return medicines.take(80)

        return medicines
            .mapNotNull { medicine ->
                val score = medicine.matchScore(normalizedQuery)
                if (score <= MAX_RESULT_SCORE) RankedMedicine(medicine, score) else null
            }
            .sortedWith(compareBy<RankedMedicine> { it.score }.thenBy { it.medicine.brandName })
            .take(80)
            .map { it.medicine }
    }

    private fun MedicineEntity.matchScore(query: String): Int {
        val brand = brandName.normalized()
        val generic = genericName.normalized()
        val aliasText = aliases.normalized()
        val categoryText = category.normalized()
        val dosageText = dosageForm.normalized()
        val fields = listOf(brand, generic, aliasText, categoryText, dosageText)

        if (fields.any { it == query }) return 0
        if (fields.any { it.startsWith(query) }) return 1
        if (fields.any { it.contains(query) }) return 2

        val queryTokens = query.split(" ").filter { it.isNotBlank() }
        val fieldTokens = fields.flatMap { it.split(" ") }.filter { it.isNotBlank() }
        if (queryTokens.isNotEmpty() && queryTokens.all { queryToken ->
                fieldTokens.any { fieldToken -> fieldToken.startsWith(queryToken) || levenshtein(queryToken, fieldToken) <= 1 }
            }) {
            return 3
        }

        val bestDistance = bestDistanceTo(query)
        val normalizedDistance = bestDistance * 100 / query.length.coerceAtLeast(1)
        val subsequencePenalty = if (fields.any { query.isSubsequenceOf(it) || it.isSubsequenceOf(query) }) 1 else 3
        return 4 + normalizedDistance + subsequencePenalty
    }

    private fun MedicineEntity.bestDistanceTo(query: String): Int {
        val brand = brandName.normalized()
        val generic = genericName.normalized()
        val candidates = buildList {
            add(brand)
            add(generic)
            add(aliases.normalized())
            addAll(brand.split(" "))
            addAll(generic.split(" "))
            addAll(aliases.normalized().split(" "))
        }.filter { it.isNotBlank() }

        return candidates.minOf { candidate ->
            val comparable = when {
                candidate.length > query.length + 3 && candidate.contains(query.take(3)) -> {
                    candidate.windowed(query.length, 1, partialWindows = false)
                        .minByOrNull { levenshtein(query, it) }
                        ?: candidate
                }
                else -> candidate
            }
            levenshtein(query, comparable)
        }
    }

    private fun String.normalized(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z0-9]+".toRegex(), " ")
            .trim()
    }

    private fun String.isSubsequenceOf(value: String): Boolean {
        if (isBlank()) return false
        var index = 0
        for (character in value) {
            if (index < length && this[index] == character) index++
        }
        return index == length
    }

    private fun levenshtein(left: String, right: String): Int {
        val costs = IntArray(right.length + 1) { it }
        for (i in 1..left.length) {
            var previous = i
            costs[0] = i
            for (j in 1..right.length) {
                val current = costs[j]
                costs[j] = minOf(
                    costs[j] + 1,
                    costs[j - 1] + 1,
                    previous + if (left[i - 1] == right[j - 1]) 0 else 1
                )
                previous = current
            }
        }
        return costs[right.length]
    }

    private data class RankedMedicine(
        val medicine: MedicineEntity,
        val score: Int
    )

    private fun List<MedicineEntity>.filteredByCategory(category: String): List<MedicineEntity> {
        return if (category == CATEGORY_ALL) this else filter { it.category == category }
    }

    private companion object {
        const val CATEGORY_ALL = "All"
        const val MIN_FUZZY_QUERY_LENGTH = 3
        const val MAX_SUGGESTION_DISTANCE = 4
        const val MAX_RESULT_SCORE = 65
    }
}
