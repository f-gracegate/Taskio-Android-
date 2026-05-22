package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<ProjectTask>> = taskDao.getAllTasks()

    fun getTasksByDate(dateString: String): Flow<List<ProjectTask>> {
        return taskDao.getTasksByDate(dateString)
    }

    fun getTasksByGroup(groupName: String): Flow<List<ProjectTask>> {
        return taskDao.getTasksByGroup(groupName)
    }

    suspend fun insert(task: ProjectTask): Long {
        return taskDao.insertTask(task)
    }

    suspend fun update(task: ProjectTask) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: ProjectTask) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteById(id: Int) {
        taskDao.deleteTaskById(id)
    }
}
