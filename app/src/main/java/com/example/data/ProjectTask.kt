package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "project_tasks")
data class ProjectTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val groupName: String, // "Office Project", "Personal Project", "Daily Study"
    val status: String = "TO_DO", // "TO_DO", "IN_PROGRESS", "COMPLETED"
    val time: String = "09:00 AM",
    val dateString: String = "2026-05-25", // Format: YYYY-MM-DD for easy querying
    val startDate: String = "01 May, 2026",
    val endDate: String = "30 Jun, 2026",
    val logoName: String = "Grocery shop", // Used for drawing logo icons: "Grocery", "Work", "Study", etc.
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM" // "HIGH", "MEDIUM", "LOW"
) : Serializable
