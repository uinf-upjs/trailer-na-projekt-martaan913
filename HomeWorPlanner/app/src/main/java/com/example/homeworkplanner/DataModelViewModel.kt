package com.example.homeworkplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DataModelViewModel(
    private val dao: Dao
): ViewModel() {
    fun saveHomework(hw: Homework){
        viewModelScope.launch(){
            dao.insert(hw)
        }
    }

    fun getAllHomeworks(): Flow<List<Homework>> {
        return dao.getAllHomeworks()
    }

    fun delete(hw: Homework) {
        viewModelScope.launch {
            dao.delete(hw)
        }
    }


//    fun saveExam(exam: Exam){
//        viewModelScope.launch(){
//            dao.insert(exam)
//        }
//    }
//
//    fun getAllExams(): Flow<List<Exam>> {
//        return dao.getAllExams()
//    }
//
//    fun delete(exam: Exam) {
//        viewModelScope.launch {
//            dao.delete(exam)
//        }
//    }
}