package com.hope.db_libs.dbmanager

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "image_items")
data class ImageItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    var imageUrl: String,
    val localPath: String,
    val savedPath: String,
    val createdAt: Date,
    var isCreated: Boolean = false,
    val isError: Boolean = false,
    val details: String,
    val processId: String,
)