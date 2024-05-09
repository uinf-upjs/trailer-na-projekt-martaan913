package com.example.homeworkplanner

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Exam(
    var subject: String,
    var title: String,
    var description: String? = null,
    var deadline: LocalDate,
    var time: LocalTime,
    var category: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null
        set(value){
            field = value
        }
}