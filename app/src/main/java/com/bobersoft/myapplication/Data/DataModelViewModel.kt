package com.bobersoft.myapplication.Data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobersoft.homeworkplanner.Exam
import com.bobersoft.myapplication.Homework
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DataModelViewModel(
    private val hwDao: Dao,
    private val examDao: ExamDao
): ViewModel() {
    fun saveHomework(hw: Homework){
        viewModelScope.launch(){
            hwDao.insert(hw)
        }
    }

    fun getAllHomeworks(): Flow<List<Homework>> {
        return hwDao.getAllHomeworks()
    }

    fun getNotFinishedHw(): Flow<List<Homework>>{
        return hwDao.getNotFinishedHomeworks()
    }

    fun delete(hw: Homework) {
        viewModelScope.launch {
            hwDao.delete(hw)
        }
    }


    fun saveExam(exam: Exam){
        viewModelScope.launch(){
            examDao.insert(exam)
        }
    }

    fun getAllExams(): Flow<List<Exam>> {
        return examDao.getAllExams()
    }

    fun delete(exam: Exam) {
        viewModelScope.launch {
            examDao.delete(exam)
        }
    }
}