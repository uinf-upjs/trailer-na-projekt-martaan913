package com.bobersoft.myapplication.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bobersoft.homeworkplanner.Exam
import com.bobersoft.myapplication.Homework

@Database(entities = [Homework::class, Exam::class], version = 2)
@TypeConverters(Converters::class)
abstract class HomeworkDatabase: RoomDatabase() {
    abstract val dao: Dao
    abstract val examDao: ExamDao

    companion object {
        // Migračné skripty
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a temporary table with the same schema as Homework,
                // but with the columns in a different order
                database.execSQL("CREATE TEMPORARY TABLE Homework_temp (" +
                        "uid INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "subject TEXT," +
                        "title TEXT," +
                        "time INTEGER," + // Add the time column here
                        "finished INTEGER DEFAULT 0," +
                        "description TEXT," +
                        "deadline INTEGER" +
                        ")")

                database.execSQL("ALTER TABLE Homework ADD COLUMN time INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Homework ADD COLUMN finished INTEGER NOT NULL DEFAULT 0")

                // Copy the data from the Homework table to the temporary table
                database.execSQL("INSERT INTO Homework_temp (uid, subject, title, time, finished, description, deadline) " +
                        "SELECT uid, subject, title, CAST(strftime('%s', time) AS INTEGER), 0, description, deadline FROM Homework")

                // Create the Exam table
                database.execSQL("CREATE TABLE IF NOT EXISTS `Exam` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT, `subject` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `deadline` INTEGER NOT NULL, `time` INTEGER NOT NULL, `category` TEXT NOT NULL)")

                // Drop the temporary table
                database.execSQL("DROP TABLE Homework_temp")
            }
        }

        @Volatile
        private var INSTANCE: HomeworkDatabase? = null

        fun getDatabase(context: Context): HomeworkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeworkDatabase::class.java,
                    "app-database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
