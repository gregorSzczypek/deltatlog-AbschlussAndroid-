package com.example.deltatlog.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.ProjectDatabase
import com.example.deltatlog.data.local.TaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TAG = "Repository"

class Repository(private val database: ProjectDatabase, private val taskDatabase: TaskDatabase) {

    val projectList: LiveData<List<Project>> = database.projectDatabaseDao.getAll()
    val taskList: LiveData<List<Task>> = taskDatabase.taskDatabaseDao.getAll()

    suspend fun insertProject(project: Project) {
        try {
            database.projectDatabaseDao.insert(project)
            Log.i("New Project", project.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert into Database: $e")
        }
    }

    suspend fun updateProject(project: Project) {
        try {
            database.projectDatabaseDao.update(project)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    suspend fun deleteProject(project: Project) {
        try {
            database.projectDatabaseDao.deleteById(project.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }

    suspend fun insertTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.insert(task)
            Log.i("New Project", task.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert into Database: $e")
        }
    }

    suspend fun updateTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.update(task)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    suspend fun deleteTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.deleteById(task.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }

    suspend fun deleteAllTasks(projectTaskId: Long) {
        try {
            taskDatabase.taskDatabaseDao.deleteAllTasks(projectTaskId)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }
}