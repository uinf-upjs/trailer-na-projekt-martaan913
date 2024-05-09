package com.example.homeworkplanner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager

class ReminderViewModel(application: Application): ViewModel() {

    private val workManager = WorkManager.getInstance(application)
}