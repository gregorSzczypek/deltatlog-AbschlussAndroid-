package com.example.deltatlog.data

import Datasource
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.ProjectDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TAG = "Repository"

class Repository(private val database: ProjectDatabase) {

    val projectList: LiveData<List<Project>> = database.projectDatabaseDao.getAll()

    suspend fun getProjects() {
        withContext(Dispatchers.IO) {
            val newProjectList = Datasource().loadProjects()
            Log.i("Projects", newProjectList.toString())
            database.projectDatabaseDao.insertAll(newProjectList)
        }
    }

//    suspend fun getProjects() {
//        withContext(Dispatchers.IO) {
//            val newDrinkList = api.retrofitService.getDrinkList().drinks
//            database.drinkDatabaseDao.insertAll(newDrinkList)
//        }
//    }

    suspend fun insert(project: Project) {
        try {
            database.projectDatabaseDao.insert(project)
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
}