package com.example.deltatlog

import TaskAdapter
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.util.TimerTask

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

    //livedata für user in unserer app
    private val _currentUser = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _loading = MutableLiveData<ApiStatus>()
    val loading: LiveData<ApiStatus>
        get() = _loading

    val _uri = MutableLiveData<String>()
    val uri: LiveData<String>
        get() = _uri

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

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.update(project)
//            _loading.value = true
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${project.id}")
            repository.delete(project)
//            _loading.value = true
        }
    }

    fun unsetComplete() {
//        _loading.value = false
    }


    fun loadTaskData() {
        viewModelScope.launch {
            try {
                repository.getTasks()
                _loading.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "Error loading Data: $e")
                if (taskList.value.isNullOrEmpty()) {
                    _loading.value = ApiStatus.ERROR
                } else {
                    _loading.value = ApiStatus.DONE
                }
            }
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
//            _loading.value = true
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
//            _loading.value = true
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${task.id}")
            repository.deleteTask(task)
//            _loading.value = true
        }
    }

    fun deleteAllTasks(projectTaskId: Long) {
        viewModelScope.launch {
            Log.d("ViewModel", "Calling repository delete with: ${projectTaskId}")
            repository.deleteAllTasks(projectTaskId)
//            _loading.value = true
        }
    }

//    fun unsetComplete() {
////        _loading.value = false
//    }

    fun signUp(context: Context, email: String, pw: String, pwConfirm: String, navController: NavController) {

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
                    _currentUser.value = firebaseAuth.currentUser
                    Toast.makeText(context, "Succesfully signed in user ${firebaseAuth.currentUser?.email}", Toast.LENGTH_LONG).show()
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }

    fun startTimer(holder: TaskAdapter.ItemViewHolder, item: Task, position: Int, itemId: Long, timers: MutableMap<Long, java.util.Timer>) {

        timers.getOrPut(itemId, {java.util.Timer()})
        val timer = timers[itemId]

        timer!!.scheduleAtFixedRate(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                holder.itemView.post {
                    val timeInMillis = System.currentTimeMillis() - item.startTime + item.elapsedTime
                    val seconds = (timeInMillis / 1000).toInt()
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    holder.taskDuration.text =
                        "${String.format("%02d", hours % 24)}:${String.format("%02d", minutes % 60)}:${String.format(
                            "%02d",
                            seconds % 60
                        )}"
                }
            }
        }, 0, 1000)
    }

    fun stopTimer(holder: TaskAdapter.ItemViewHolder, item:Task, position: Int, itemId: Long, adapter: TaskAdapter, timers: MutableMap<Long, java.util.Timer>) {
        val timer = timers[itemId]
        timer!!.cancel()
        item.duration = holder.taskDuration.text.toString()
        adapter.notifyItemChanged(position)
//        updateTask(item)
        timers.remove(itemId)
    }
}

