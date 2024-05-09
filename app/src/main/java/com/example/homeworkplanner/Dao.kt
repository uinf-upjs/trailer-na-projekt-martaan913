package com.example.homeworkplanner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM Homework")
    fun getAllHomeworks(): Flow<List<Homework>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(homework: Homework)

    @Delete
    suspend fun delete(homework: Homework)

    @Query("DELETE FROM homework WHERE title=:title")
    suspend fun deleteHomework(title: String)
}