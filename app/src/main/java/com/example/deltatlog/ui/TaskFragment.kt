package com.example.deltatlog.ui

import TaskAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.util.ExportManager
import com.example.deltatlog.util.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.util.TaskSnapshotListener
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.getProjectDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class TaskFragment : Fragment() {

    private val taskFragmentViewModel: viewModel by viewModels()
    private lateinit var taskFragmentBinding: FragmentTaskBinding
    private var projectId: Long = 0
    private var color: String? = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private val firebaseManager = FirebaseManager()
    private val exportManager = ExportManager()
    private lateinit var taskSnapshotListener: TaskSnapshotListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        taskFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_task,
            container,
            false
        )

        // Hole die projectId aus den Argumenten
        projectId = requireArguments().getLong("projectId")
        Log.i("projectID", projectId.toString())
        color = requireArguments().getString("color")

        taskFragmentBinding.taskList.setHasFixedSize(true) // set fixed size for recyclerview

        // Inflate the layout for this fragment
        return taskFragmentBinding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser!!.uid
        val currentUserEmail = firebaseAuth.currentUser!!.email
        taskSnapshotListener = TaskSnapshotListener(this, currentUserId)
        // BackButton Navigation in Toolbar
        taskFragmentBinding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToHomeFragment())
        }

        // Set onClickListener on menu item logout
        taskFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    taskSnapshotListener.stopListening()
                    firebaseManager.logOut(
                        firebaseAuth,
                        currentUserEmail!!,
                        requireContext()
                    )
                    taskFragmentViewModel.databaseDeleted = false
                    findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToLoginFragment())
                }

                R.id.export -> {
                    // Retrieve the tasks related to the current project from the ViewModel
                    // Handle export menu item click
                    taskFragmentViewModel.taskList.value?.let { tasks ->
                        exportManager.exportTasksToCSV(tasks, requireContext())
                    }
                }
            }
            true
        }

        val recyclerView = taskFragmentBinding.taskList

        taskFragmentViewModel.taskList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter = TaskAdapter(
                    taskFragmentViewModel,
                    requireContext(),
                    it.filter { it.taskProjectId == projectId },
                    findNavController(),
                    projectId,
                    color,
                    lifecycleScope
                )
            }
        )

        taskFragmentBinding.floatingActionButton.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_task, null)
            val newTaskName = dialogLayout.findViewById<EditText>(R.id.input_task_name)
            val newTaskDescription =
                dialogLayout.findViewById<EditText>(R.id.input_task_description)

            with(builder) {
                setTitle("New Task")
                setPositiveButton("Ok") { dialog, which ->
                    val newTaskNameString = newTaskName.text.toString()
                    val newTaskDescriptionString = newTaskDescription.text.toString()
                    val newTask = Task(taskProjectId = projectId, color = color.toString())

                    if (newTaskNameString != "") {
                        newTask.name = newTaskNameString
                    }
                    if (newTaskDescriptionString != "") {
                        newTask.notes = newTaskDescriptionString
                    }
                    taskFragmentViewModel.insertTask(newTask) {

                        val database = getTaskDatabase(context)

                        // Launch a coroutine
                        lifecycleScope.launch {
                            // Perform the database operation within the coroutine
                            val taskList: List<Task> = withContext(Dispatchers.IO) {
                                database.taskDatabaseDao.getAllNLD()
                            }

                            val task2Add = taskList.last()

                            Log.d("TaskFragment", currentUserId)

                            val attributes = hashMapOf<String, Any>(
                                "id" to task2Add.id,
                                "taskProjectId" to task2Add.taskProjectId,
                                "name" to task2Add.name,
                                "color" to task2Add.color,
                                "date" to task2Add.date,
                                "duration" to task2Add.duration,
                                "description" to task2Add.description,
                                "notes" to task2Add.notes,
                                "elapsedTime" to task2Add.elapsedTime
                            )

                            Log.d("firebasePTID", task2Add.id.toString())

                            firebaseManager.addTask(task2Add, attributes)

                            // Update number of tasks in project Object
                            val project = withContext(Dispatchers.IO) {
                                getProjectDatabase(context).projectDatabaseDao.getAllNLD()
                                    .find { it.id == projectId }
                            }
                            val tasks = withContext(Dispatchers.IO) {
                                getTaskDatabase(context).taskDatabaseDao.getAllNLD()
                                    .filter { it.taskProjectId == projectId }
                            }
                            val tasksSize = tasks.size

                            project!!.numberOfTasks = tasksSize.toLong()
                            var totalTime = 0L

                            for (i in tasks) {
                                totalTime += i.elapsedTime
                            }

                            val hours = totalTime / 3600
                            val minutes = (totalTime % 3600) / 60
                            val sec = totalTime % 60
                            val timeString =
                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d:%02d",
                                    hours,
                                    minutes,
                                    sec
                                )

                            Log.e("totalTime", timeString)

                            project.totalTime = timeString

                            taskFragmentViewModel.updateProject(project)

                            val updates = mutableMapOf<String, Any>(
                                "numberOfTasks" to tasksSize,
                                "totalTime" to timeString
                            )

                            firebaseManager.updateProjectChanges(project.id.toString(), updates)

                            Toast.makeText(
                                context,
                                "$newTaskNameString created",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Task creation cancelled by user",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                setView(dialogLayout)
                show()
            }
        }
        // here firebase stuff
        val database = getTaskDatabase(requireContext())
        taskSnapshotListener.startListening(database)
    }
}