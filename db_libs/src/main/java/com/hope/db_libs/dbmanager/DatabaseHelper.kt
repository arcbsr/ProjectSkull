package com.hope.db_libs.dbmanager

import android.content.Context
import androidx.room.Room

object DatabaseHelper {
    fun getDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "image_db"
        ).fallbackToDestructiveMigration()  // Automatically handle migrations or destructive ones
            .build()
    }
}
