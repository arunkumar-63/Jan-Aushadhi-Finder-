package com.janaushadhi.finder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MedicineEntity::class, ReminderEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jan_aushadhi_finder.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(seedCallback)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val seedCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (database.medicineDao().count() == 0) {
                            database.medicineDao().insertAll(SeedData.generateMedicines())
                        }
                    }
                }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE medicines ADD COLUMN category TEXT NOT NULL DEFAULT 'General'")
                db.execSQL("ALTER TABLE medicines ADD COLUMN dosageForm TEXT NOT NULL DEFAULT 'Tablet'")
                db.execSQL("ALTER TABLE medicines ADD COLUMN aliases TEXT NOT NULL DEFAULT ''")
                db.execSQL("DELETE FROM medicines")
            }
        }
    }
}
