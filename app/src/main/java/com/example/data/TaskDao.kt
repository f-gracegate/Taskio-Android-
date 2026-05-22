package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM project_tasks ORDER BY dateString ASC, time ASC")
    fun getAllTasks(): Flow<List<ProjectTask>>

    @Query("SELECT * FROM project_tasks WHERE dateString = :dateString ORDER BY time ASC")
    fun getTasksByDate(dateString: String): Flow<List<ProjectTask>>

    @Query("SELECT * FROM project_tasks WHERE groupName = :groupName ORDER BY dateString ASC")
    fun getTasksByGroup(groupName: String): Flow<List<ProjectTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ProjectTask): Long

    @Update
    suspend fun updateTask(task: ProjectTask)

    @Delete
    suspend fun deleteTask(task: ProjectTask)

    @Query("DELETE FROM project_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}
