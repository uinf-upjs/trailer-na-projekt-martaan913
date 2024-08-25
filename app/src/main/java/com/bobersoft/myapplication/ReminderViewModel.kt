package com.bobersoft.myapplication
//
//import android.app.Application
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import androidx.work.workDataOf
//import com.example.myapplication.Homework
//import java.util.Objects
//import java.util.concurrent.TimeUnit
//import javax.sql.DataSource
//
//class ReminderViewModel(application: Application): ViewModel() {
//
//    private val workManager = WorkManager.getInstance(application)
//
//    internal fun scheduleReminder(
//        duration: Long,
//        unit: TimeUnit,
//        plantName: String
//    ) {
//        // create a Data instance with the plantName passed to it
//        val myWorkRequestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
//        for (items in itemsList.toMutableList()) {
//            if (items.title == plantName) {
//                myWorkRequestBuilder.setInputData(
//                    workDataOf(
//                        "NAME" to items.title,
//                        "MESSAGE" to items.description
//                    )
//                )
//            }
//        }
//        myWorkRequestBuilder.setInitialDelay(duration, unit)
//        workManager.enqueue(myWorkRequestBuilder.build())
//    }
//}
//
//class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            ReminderViewModel(application) as T
//        } else {
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
//}