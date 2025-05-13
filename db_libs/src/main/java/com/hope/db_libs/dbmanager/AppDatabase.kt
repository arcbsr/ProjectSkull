package com.hope.db_libs.dbmanager

import android.content.Context
import androidx.room.*

@Database(entities = [ImageItem::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageItemDao(): ImageItemDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "image_db"
                )
                    .build().also { instance = it }
            }
        }

    }
}
