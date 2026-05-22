package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ProjectTask
import com.example.data.TaskDatabase
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Data class to represent grouped progress on the HomeScreen
data class TaskGroupStats(
    val groupName: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val completionPercentage: Int, // 0 to 100
    val logoName: String,
    val colorHex: String
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val database = TaskDatabase.getDatabase(application, viewModelScope)
        repository = TaskRepository(database.taskDao())
    }

    // List of all tasks reactively
    val allTasks: StateFlow<List<ProjectTask>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current selected date string for list rendering. Default is '2026-05-25' (Monday)
    private val _selectedDate = MutableStateFlow("2026-05-25")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Current filter selection: "All", "To do", "In Progress", "Completed"
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Tasks loaded for the active selected date, filtered by category
    val filteredTasks: StateFlow<List<ProjectTask>> = combine(
        allTasks,
        _selectedDate,
        _selectedFilter
    ) { tasks, date, filter ->
        tasks.filter { task ->
            val matchesDate = task.dateString == date
            val matchesFilter = when (filter) {
                "All" -> true
                "To do" -> task.status == "TO_DO" && !task.isCompleted
                "In Progress" -> task.status == "IN_PROGRESS" && !task.isCompleted
                "Completed" -> task.isCompleted || task.status == "COMPLETED"
                else -> true
            }
            matchesDate && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Derived: Tasks currently "In Progress" across all dates for the home horizontal bar
    val inProgressTasks: StateFlow<List<ProjectTask>> = allTasks.map { tasks ->
        tasks.filter { it.status == "IN_PROGRESS" && !it.isCompleted }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active project group progress data for home lists
    val taskGroupStats: StateFlow<List<TaskGroupStats>> = allTasks.map { tasks ->
        val groups = listOf("Office Project", "Personal Project", "Daily Study")
        val defaultColors = mapOf(
            "Office Project" to "#FB6F8B",  // Cute vibrant pink
            "Personal Project" to "#5C53FF", // Bold workspace indigo
            "Daily Study" to "#FFAC30"       // Sunshine gold
        )
        val defaultLogos = mapOf(
            "Office Project" to "Grocery shop",
            "Personal Project" to "Personal",
            "Daily Study" to "Study"
        )

        groups.map { group ->
            val filtered = tasks.filter { it.groupName == group }
            val total = filtered.size
            val completed = filtered.count { it.isCompleted || it.status == "COMPLETED" }
            val percentage = if (total > 0) (completed * 100) / total else 0

            TaskGroupStats(
                groupName = group,
                totalTasks = total,
                completedTasks = completed,
                completionPercentage = percentage,
                logoName = defaultLogos[group] ?: "Work",
                colorHex = defaultColors[group] ?: "#5C53FF"
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Computed overall today progress indicator percent (e.g. 85%)
    val todayCompletionPercentage: StateFlow<Int> = combine(allTasks, _selectedDate) { tasks, date ->
        val todayList = tasks.filter { it.dateString == date }
        if (todayList.isEmpty()) {
            85 // Return 85% mock representation as default matching screens when empty
        } else {
            val completedCount = todayList.count { it.isCompleted || it.status == "COMPLETED" }
            (completedCount * 100) / todayList.size
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 85
    )

    fun changeSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun changeSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Toggle completion status
    fun toggleTaskCompletion(task: ProjectTask) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                status = if (!task.isCompleted) "COMPLETED" else "TO_DO"
            )
            repository.update(updated)
        }
    }

    // Add a pre-configured project/task from our AddProjectScreen UI
    fun addProjectTask(
        title: String,
        description: String,
        groupName: String,
        time: String,
        dateString: String,
        startDate: String,
        endDate: String,
        logoName: String
    ) {
        viewModelScope.launch {
            val task = ProjectTask(
                title = title,
                description = description,
                groupName = groupName,
                status = "TO_DO",
                time = time,
                dateString = dateString,
                startDate = startDate,
                endDate = endDate,
                logoName = logoName,
                isCompleted = false
            )
            repository.insert(task)
        }
    }

    // Delete tasks if the user actions delete in tasks list
    fun deleteTask(task: ProjectTask) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}

// Simple Factory for instantiating TaskViewModel with Android Context Application support
class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
