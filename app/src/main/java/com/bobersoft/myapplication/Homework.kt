package com.bobersoft.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Homework(
    var subject: String,
    var title: String,
    var description: String? = null,
    var deadline: LocalDate,
    var time: LocalTime,
    var finished: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null
        set(value){
            field = value
        }
}

