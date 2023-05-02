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

    suspend fun getProjects() {
        withContext(Dispatchers.IO) {
            val newProjectList = database.projectDatabaseDao.getAll()
//            database.projectDatabaseDao.insertAll(newProjectList)
        }
    }

    suspend fun insert(project: Project) {
        try {
            database.projectDatabaseDao.insert(project)
            Log.i("New Project", project.toString())
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert into Database: $e")
        }
    }

    //todo: Update
    suspend fun update(project: Project) {
        try {
            database.projectDatabaseDao.update(project)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    //todo: delete
    suspend fun delete(project: Project) {
        try {
            database.projectDatabaseDao.deleteById(project.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }


    suspend fun getTasks() {
        withContext(Dispatchers.IO) {
            val newTaskList = taskDatabase.taskDatabaseDao.getAll()
//            database.projectDatabaseDao.insertAll(newProjectList)
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

    //todo: Update
    suspend fun updateTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.update(task)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update Database: $e")
        }
    }

    //todo: delete
    suspend fun deleteTask(task: Task) {
        try {
            taskDatabase.taskDatabaseDao.deleteById(task.id)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to delete Database entry: $e")
        }
    }
}