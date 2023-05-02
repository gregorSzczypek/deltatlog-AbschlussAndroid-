package com.example.deltatlog

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.deltatlog.data.Repository
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getDatabase
import kotlinx.coroutines.launch

const val TAG = "SharedViewModel"

enum class ApiStatus { LOADING, ERROR, DONE }

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = Repository(database)

    val projectList = repository.projectList

//    init {
//        loadData()
//    }

    private val _loading = MutableLiveData<ApiStatus>()
    val loading: LiveData<ApiStatus>
        get() = _loading

    fun loadData() {
        viewModelScope.launch {
            try {
                repository.getProjects()
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "Error loading Data: $e")
                if (projectList.value.isNullOrEmpty()) {
                    _loading.value = ApiStatus.ERROR
                } else {
                    _loading.value = ApiStatus.DONE
                }
            }
        }
    }

    fun insertProject(project: Project) {
        viewModelScope.launch {
            repository.insert(project)
//            _loading.value = true
        }
    }

    //todo: Update guest
    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.update(project)
//            _loading.value = true
        }
    }

    //todo: Delete guest
    fun deleteProject(project: Project) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${project.id}")
            repository.delete(project)
//            _loading.value = true
        }
    }

    //todo: unset complete
    fun unsetComplete() {
//        _loading.value = false
    }
}