package com.example.homeworkplanner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Query("SELECT * FROM Exam")
    fun getAllExams(): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exam: Exam)

    @Delete
    suspend fun delete(exam: Exam)

    @Query("DELETE FROM Exam WHERE title=:title")
    suspend fun deleteExam(title: String)
}