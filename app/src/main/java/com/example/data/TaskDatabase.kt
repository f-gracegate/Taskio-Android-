package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProjectTask::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "taskio_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(TaskDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.taskDao())
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDao) {
            // Seed Office Project Tasks
            taskDao.insertTask(
                ProjectTask(
                    title = "Market Research",
                    description = "This application is designed for super shops. By using this application they can enlist all their products.",
                    groupName = "Office Project",
                    status = "COMPLETED",
                    time = "10:00 AM",
                    dateString = "2026-05-25",
                    startDate = "01 May, 2026",
                    endDate = "30 Jun, 2026",
                    logoName = "Grocery shop",
                    isCompleted = true,
                    priority = "HIGH"
                )
            )
            taskDao.insertTask(
                ProjectTask(
                    title = "Competitive Analysis",
                    description = "This application is designed for super shops. An analysis of competitor offerings in the supermarket space.",
                    groupName = "Office Project",
                    status = "IN_PROGRESS",
                    time = "12:00 PM",
                    dateString = "2026-05-25",
                    startDate = "01 May, 2026",
                    endDate = "30 Jun, 2026",
                    logoName = "Grocery shop",
                    isCompleted = false,
                    priority = "MEDIUM"
                )
            )
            taskDao.insertTask(
                ProjectTask(
                    title = "How to pitch a Design Sprint",
                    description = "About design sprint - prepare slides for explaining key steps of design sprint to stakeholders.",
                    groupName = "Office Project",
                    status = "TO_DO",
                    time = "09:00 PM",
                    dateString = "2026-05-25",
                    startDate = "15 May, 2026",
                    endDate = "31 May, 2026",
                    logoName = "Work",
                    isCompleted = false,
                    priority = "HIGH"
                )
            )

            // Seed Personal Project Tasks (Uber Eats Redesign)
            taskDao.insertTask(
                ProjectTask(
                    title = "Create Low-fidelity Wireframe",
                    description = "Uber Eats redesign challenge - map out core navigation, product detail page, and basket states.",
                    groupName = "Personal Project",
                    status = "TO_DO",
                    time = "07:00 PM",
                    dateString = "2026-05-25",
                    startDate = "10 May, 2026",
                    endDate = "15 Jun, 2026",
                    logoName = "Personal",
                    isCompleted = false,
                    priority = "LOW"
                )
            )
            taskDao.insertTask(
                ProjectTask(
                    title = "Competitor UX Audit",
                    description = "Audit core competitor food delivery applications (Deliveroo, JustEat) to record seamless animation loops.",
                    groupName = "Personal Project",
                    status = "COMPLETED",
                    time = "03:00 PM",
                    dateString = "2026-05-24",
                    startDate = "10 May, 2026",
                    endDate = "15 Jun, 2026",
                    logoName = "Personal",
                    isCompleted = true,
                    priority = "MEDIUM"
                )
            )

            // Seed Daily Study Tasks
            taskDao.insertTask(
                ProjectTask(
                    title = "Kotlin Flow Deep Dive",
                    description = "Complete intermediate Flow and StateFlow courses covering thread switching and channel buffers.",
                    groupName = "Daily Study",
                    status = "COMPLETED",
                    time = "08:00 AM",
                    dateString = "2026-05-25",
                    startDate = "01 May, 2026",
                    endDate = "31 Dec, 2026",
                    logoName = "Study",
                    isCompleted = true,
                    priority = "LOW"
                )
            )
            taskDao.insertTask(
                ProjectTask(
                    title = "Jetpack Compose Animation Basics",
                    description = "Practicing modern Compose animations like animateContentSize and updateTransition inside lists.",
                    groupName = "Daily Study",
                    status = "TO_DO",
                    time = "10:30 PM",
                    dateString = "2026-05-25",
                    startDate = "01 May, 2026",
                    endDate = "31 Dec, 2026",
                    logoName = "Study",
                    isCompleted = false,
                    priority = "HIGH"
                )
            )
        }
    }
}
