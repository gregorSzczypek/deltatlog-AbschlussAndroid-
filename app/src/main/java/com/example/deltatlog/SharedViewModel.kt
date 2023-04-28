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

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = Repository(database)

    val projectList = repository.projectList

    private val _complete = MutableLiveData<Boolean>()
    val complete: LiveData<Boolean>
        get() = _complete

    fun insertProject(project: Project) {
        viewModelScope.launch {
            repository.insert(project)
            _complete.value = true
        }
    }

    //todo: Update guest
    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.update(project)
            _complete.value = true
        }
    }

    //todo: Delete guest
    fun deleteProject(project: Project) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${project.id}")
            repository.delete(project)
            _complete.value = true
        }
    }

    //todo: unset complete
    fun unsetComplete() {
        _complete.value = false
    }
}