package com.janaushadhi.finder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brandName: String,
    val genericName: String,
    val brandPrice: Double,
    val genericPrice: Double,
    val category: String = "General",
    val dosageForm: String = "Tablet",
    val aliases: String = ""
) {
    val savings: Double get() = brandPrice - genericPrice
}
