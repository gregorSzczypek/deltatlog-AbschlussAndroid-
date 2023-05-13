package com.example.deltatlog

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.deltatlog.data.Repository
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.ui.LoginFragmentDirections
import com.example.deltatlog.ui.SignUpFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

const val TAG = "SharedViewModel"

enum class ApiStatus { LOADING, ERROR, DONE }

class viewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val taskDatabase = getTaskDatabase(application)
    private val repository = Repository(database, taskDatabase)
    val projectList = repository.projectList
    val taskList = repository.taskList
    //instanz von firebase
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun loadProjectData() {
        viewModelScope.launch {
            try {
                repository.getProjects()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading Data: $e")
            }
        }
    }

    fun insertProject(project: Project) {
        viewModelScope.launch {
            repository.insertProject(project)
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
            repository.delete(project)
        }
    }

    fun loadTaskData() {
        viewModelScope.launch {
            try {
                repository.getTasks()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading Data: $e")
            }
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
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

    fun signUp(
        context: Context,
        email: String,
        pw: String,
        pwConfirm: String,
        navController: NavController
    ) {

        // Check of valid input and calling register method from firebase object
        if (email.isNotEmpty() && pw.isNotEmpty() && pwConfirm.isNotEmpty()) {

            if (pw == pwConfirm) {
                firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Succesfully registered", Toast.LENGTH_SHORT).show()
                        // Navigation to login page after registration
                        navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                    } else {
                        Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }

    fun login(context: Context, email: String, pw: String, navController: NavController) {
        // Check of valid input and calling login method from firebase object
        if (email.isNotEmpty() && pw.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Succesfully signed in user ${firebaseAuth.currentUser?.email}",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }
}

