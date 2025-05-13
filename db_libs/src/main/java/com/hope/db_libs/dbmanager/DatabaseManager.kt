package com.hope.db_libs.dbmanager

import android.content.Context

object DatabaseManager {
    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = AppDatabase.getInstance(context)
        }
    }

    fun imageItemDao(): ImageItemDao {
        return requireNotNull(database) {
            "DatabaseManager not initialized. Call DatabaseManager.initialize(context) first."
        }.imageItemDao()
    }
}