package com.example.homeworkplanner

class HomeworksRepository (private val homeworkDao: Dao){

    val homeworks = homeworkDao.getAllHomeworks()
}