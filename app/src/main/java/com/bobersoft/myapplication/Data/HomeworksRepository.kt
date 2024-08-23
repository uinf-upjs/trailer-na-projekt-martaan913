package com.bobersoft.myapplication.Data

class HomeworksRepository (private val homeworkDao: Dao){

    val homeworks = homeworkDao.getAllHomeworks()
}