package com.example.homeworkplanner

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    var isDarkTheme: Boolean by mutableStateOf(false)

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
}