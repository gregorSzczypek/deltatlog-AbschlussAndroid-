package com.example.deltatlog.data.local


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.deltatlog.data.datamodels.Task

/**
 * Data Access Object for the RoomDatabase
 */

@Dao
interface TaskDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM taskTable")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM taskTable")
    fun getAllNLD(): List<Task>

    @Query("DELETE FROM taskTable WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM taskTable WHERE taskProjectId = :id")
    suspend fun deleteAllTasks(id: Long)

    @Query("DELETE FROM taskTable")
    suspend fun deleteAllTasks()

    @Delete
    suspend fun deleteTasks(tasks: List<Task>)
}