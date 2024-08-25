package com.bobersoft.myapplication.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bobersoft.myapplication.Homework
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

    @Query("SELECT * FROM Homework WHERE finished = 0")
    fun getNotFinishedHomeworks(): Flow<List<Homework>>
}