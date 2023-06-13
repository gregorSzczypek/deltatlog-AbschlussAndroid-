package com.example.deltatlog

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.deltatlog.data.Repository
import com.example.deltatlog.data.datamodels.Logo
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.getProjectDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.data.remote.LogoApi
import kotlinx.coroutines.launch

const val TAG = "SharedViewModel"

class viewModel(application: Application) : AndroidViewModel(application) {

    private val database = getProjectDatabase(application)
    private val taskDatabase = getTaskDatabase(application)
    private val repository = Repository(database, taskDatabase, LogoApi)

    // LiveData variables to observe data changes
    val projectList = repository.projectList
    val taskList = repository.taskList
    val logoLiveData: LiveData<Logo> = repository.logo

    var databaseDeleted = false

    // List of colors for UI
    val colors = listOf(
        R.color.colorPicker1,
        R.color.colorPicker2,
        R.color.colorPicker3,
        R.color.colorPicker4,
        R.color.colorPicker5,
        R.color.colorPicker6,
        R.color.colorPicker7,
        R.color.colorPicker8,
        R.color.colorPicker9,
        R.color.colorPicker10,
        R.color.colorPicker11,
        R.color.colorPicker12,
        R.color.colorPicker13,
        R.color.colorPicker14,
        R.color.colorPicker15,
        R.color.colorPicker16,
        R.color.colorPicker17,
        R.color.colorPicker18,
        R.color.colorPicker19,
        R.color.colorPicker20,
    )

    // Load logo from API
    fun loadLogo(companyName: String, callback: () -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "(1) API CALL")
            repository.getLogo(companyName)
            callback.invoke()
        }
    }

    // Insert a project
    fun insertProject(project: Project, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertProject(project)
            callback.invoke()
        }
    }

    // update a project
    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    // delete a project
    fun deleteProject(project: Project) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${project.id}")
            repository.deleteProject(project)
        }
    }

    // insert a task
    fun insertTask(task: Task, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertTask(task)
            callback.invoke()
        }
    }

    // update a task
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    // delete a task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${task.id}")
            repository.deleteTask(task)
        }
    }

    // delete all task
    fun deleteAllTasks(projectTaskId: Long) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${projectTaskId}")
            repository.deleteAllTasks(projectTaskId)
        }
    }
}

