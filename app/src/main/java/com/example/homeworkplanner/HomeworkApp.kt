package com.example.homeworkplanner

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import java.util.Locale

class HomeworkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "CHANNEL_ID",
            "High priority notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Načítajte jazyk z SharedPreferences
        val sharedPref = applicationContext.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString(getString(R.string.language_key), "default_language_value")

        // Nastavte jazyk aplikácie
        if (selectedLanguage != null) {
            setLocale(selectedLanguage)
        }
    }

    private fun setLocale(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}