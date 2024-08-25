package com.bobersoft.homeworkplanner

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import java.util.Locale

class HomeworkApp : Application() {
    override fun onCreate() {
        super.onCreate()

//        val channel = NotificationChannel(
//            "CHANNEL_ID",
//            "High priority notifications",
//            NotificationManager.IMPORTANCE_HIGH
//        )

        val notificationChannel = NotificationChannel(
            "homeWork_notification",
            "Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)

        // Načítajte jazyk z SharedPreferences
        val sharedPref = applicationContext.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString(getString(R.string.language_key), "default_language_value")

        // Nastavte jazyk aplikácie
        if (selectedLanguage != null) {
            setLocale(selectedLanguage)
        }
    }

    companion object {
        const val CHANNEL_ID = "reminder_id"
    }

    private fun setLocale(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}