package com.example.deltatlog.data

import android.util.Log
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

    // Get all projects from the project database
    val projectList = database.projectDatabaseDao.getAll()

    // Get all tasks from the task database
    val taskList = taskDatabase.taskDatabaseDao.getAll()

    val logo = MutableLiveData<Logo>()

    // Get a logo for the given company name
    suspend fun getLogo(companyName: String) {
        try {
            // make API call to get the logo
            logo.value = api.retrofitService.getLogo(companyName, authHeader = authHeader)
        } catch (e: Exception){
            // Handle API call failure
            Log.d("Repository", "API Call failed: $e")
            logo.value = Logo("nothing", "nothing", "nothing")
        }
    }

    // insert a project into the project database
    suspend fun insertProject(project: Project) {
        try {
            database.projectDatabaseDao.insert(project)
            Log.i("New Project", project.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert into Database: $e")
        }
    }

    // update a project in the projectdatabase
    suspend fun updateProject(project: Project) {
        try {
            database.projectDatabaseDao.update(project)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    // delete a project from the project database
    suspend fun deleteProject(project: Project) {
        try {
            database.projectDatabaseDao.deleteById(project.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }

    // insert a task into the task database
    suspend fun insertTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.insert(task)
            Log.i("New Project", task.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert into Database: $e")
        }
    }

    // update a task in the task database
    suspend fun updateTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.update(task)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    // delete a task from the task database
    suspend fun deleteTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.deleteById(task.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }

    // delete all tasks related to a project from the task database
    suspend fun deleteAllTasks(projectTaskId: Long) {
        try {
            taskDatabase.taskDatabaseDao.deleteAllTasks(projectTaskId)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }
}