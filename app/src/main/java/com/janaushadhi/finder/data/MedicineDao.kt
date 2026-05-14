package com.janaushadhi.finder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY brandName LIMIT 80")
    fun observePopularMedicines(): Flow<List<MedicineEntity>>

    @Query(
        """
        SELECT * FROM medicines
        WHERE lower(brandName) LIKE '%' || lower(:query) || '%'
           OR lower(genericName) LIKE '%' || lower(:query) || '%'
        ORDER BY brandName
        LIMIT 80
        """
    )
    fun searchMedicines(query: String): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines ORDER BY brandName")
    suspend fun getAllMedicinesOnce(): List<MedicineEntity>

    @Query("SELECT * FROM medicines ORDER BY brandName")
    fun observeAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT COUNT(*) FROM medicines")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicines: List<MedicineEntity>)
}
