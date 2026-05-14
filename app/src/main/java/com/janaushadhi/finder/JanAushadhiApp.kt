package com.janaushadhi.finder

import android.app.Application
import com.janaushadhi.finder.data.AppDatabase
import com.janaushadhi.finder.repository.MedicineRepository

class JanAushadhiApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: MedicineRepository by lazy {
        MedicineRepository(database.medicineDao(), database.reminderDao())
    }
}
