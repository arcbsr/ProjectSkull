package com.hope.db_libs.dbmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ImageItemDao {

    @Insert
    suspend fun insert(item: ImageItem): Long

    @Update
    suspend fun update(item: ImageItem)


    @Query("SELECT * FROM image_items ORDER BY createdAt ASC")
    suspend fun getAllData(): List<ImageItem>

    @Query("SELECT * FROM image_items WHERE aiAgent = :aiAgent ORDER BY createdAt ASC")
    suspend fun getImagesByAgent(aiAgent: String): List<ImageItem>

    @Query("SELECT * FROM image_items ORDER BY createdAt DESC LIMIT 2")
    suspend fun getRecentData(): List<ImageItem>

    @Query("SELECT * FROM image_items WHERE id = :id")
    suspend fun getById(id: Int): ImageItem?

    @Query("SELECT * FROM image_items WHERE processId = :processId")
    suspend fun getByProcessId(processId: String): ImageItem?


    @Query("DELETE FROM image_items")
    suspend fun clearAll()

    @Query("DELETE FROM image_items WHERE id = :id")
    suspend fun deleteById(id: Int)

}
