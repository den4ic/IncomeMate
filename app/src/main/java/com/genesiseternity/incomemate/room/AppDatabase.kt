package com.genesiseternity.incomemate.room

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.genesiseternity.incomemate.room.entities.*

@Database(
    version = AppDatabase.VERSION,
    entities = [
        CurrencyDetailsEntity::class,
        CurrencyColorEntity::class,
        CurrencySettingsEntity::class,
        PieChartCategoriesEntity::class,
        PieChartCategoriesTitleEntity::class,
        PieChartEntity::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val VERSION: Int = 1

        /*
        private var dbInstance: AppDatabase? = null

        fun getAppDatabaseInstance(context: Context): AppDatabase
        {
            if (dbInstance == null)
            {
                dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "IncomeDatabase")
                    .allowMainThreadQueries()
                    .build()
            }

            return dbInstance!!
        }

         */
    }

    abstract fun getCurrencyDetailsDao(): CurrencyDetailsDao
    abstract fun getCurrencyColorDao(): CurrencyColorDao
    abstract fun getCurrencySettingsDao(): CurrencySettingsDao
    abstract fun getPieChartCategoriesDao(): PieChartCategoriesDao
    abstract fun getPieChartCategoriesTitleDao(): PieChartCategoriesTitleDao
    abstract fun getPieChartDao(): PieChartDao
}