package com.example.deltatlog.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.deltatlog.data.datamodels.Logo
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.ProjectDatabase
import com.example.deltatlog.data.local.TaskDatabase
import com.example.deltatlog.data.remote.LogoApi

val TAG = "Repository"

class Repository(
    private val database: ProjectDatabase,
    private val taskDatabase: TaskDatabase,
    private val api: LogoApi
) {
    val API_KEY = "sk_1c15a6f5d0a52350c5e50ff9abcb24b1"
    val authHeader = "Bearer $API_KEY"

    // TODO Müssen die nächsten zwei Zeilen live data sein??
    val projectList = database.projectDatabaseDao.getAll()
    val taskList = taskDatabase.taskDatabaseDao.getAll()

    val logo = MutableLiveData<Logo>()
    suspend fun getLogo(companyName: String) {
        try {
            logo.value = api.retrofitService.getLogo(companyName, authHeader = authHeader)

            Log.d("Repository","(2) " + logo.value!!.name)
            Log.d("Repository","(3) " + logo.value!!.domain)
            Log.d("Repository","(4) " + logo.value!!.logo)

        } catch (e: Exception){
            Log.d("Repository", "API Call failed: $e")
            logo.value = Logo("nothing", "nothing", "nothing")
        }
    }

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