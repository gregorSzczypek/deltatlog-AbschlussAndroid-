package com.example.deltatlog

import android.app.Application
import android.content.Context
import android.media.tv.TvContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.deltatlog.data.Repository
import com.example.deltatlog.data.datamodels.Logo
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.data.remote.LogoApi
import com.example.deltatlog.data.remote.LogoApiService
import com.example.deltatlog.ui.LoginFragmentDirections
import com.example.deltatlog.ui.SignUpFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Callback
import retrofit2.Retrofit

const val TAG = "SharedViewModel"

class viewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val taskDatabase = getTaskDatabase(application)
    private val repository = Repository(database, taskDatabase, LogoApi)
    val projectList = repository.projectList
    val taskList = repository.taskList
    val logoLiveData: LiveData<Logo> = repository.logo
//    var databaseDeleted = false

    fun loadLogo(companyName: String, callback: () -> Unit) {
        viewModelScope.launch {
            Log.d(TAG, "(1) API CALL")
            repository.getLogo(companyName)
            callback.invoke()
        }
    }

    fun insertProject(project: Project, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertProject(project)
            callback.invoke()
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${project.id}")
            repository.deleteProject(project)
        }
    }

    fun insertTask(task: Task, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertTask(task)
            callback.invoke()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${task.id}")
            repository.deleteTask(task)
        }
    }

    fun deleteAllTasks(projectTaskId: Long) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${projectTaskId}")
            repository.deleteAllTasks(projectTaskId)
        }
    }
}

