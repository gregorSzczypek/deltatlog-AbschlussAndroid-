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
import com.example.deltatlog.util.TaskFragmentAnimator
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
    private val animator = TaskFragmentAnimator()

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

        // retrieve the projects ID from the arguments
        projectId = requireArguments().getLong("projectId")
        Log.i("projectID", projectId.toString())
        // retrieve the color from the arguments
        color = requireArguments().getString("color")

        // set recycler view to fixed size
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

        // set onClickListener to BackButton Navigation in Toolbar
        taskFragmentBinding.materialToolbar.setNavigationOnClickListener {
            // Navigate to Home Fragment
            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToHomeFragment())
        }

        // Set onClickListener on menu item logout
        taskFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // destroy the Firebase snapshot listener
                            taskSnapshotListener.stopListening()
                            // call the logout method from firebaseManager
                            firebaseManager.logOut(firebaseAuth, currentUserEmail!!, requireContext())
                            // set the check variable for database deletion to false
                            taskFragmentViewModel.databaseDeleted = false
                            // navigate to the login fragment
                            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToLoginFragment())
                        }
                        .setNegativeButton("No", null)
                        .create()

                    alertDialog.show()
                }

                R.id.export -> {
                    // Retrieve the tasks related to the current project from the ViewModel
                    // Handle export menu item click

                    // start a coroutine
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            // get a list of projects from the projectdatabase within the coroutine
                            val projectDB =
                                getProjectDatabase(requireContext()).projectDatabaseDao.getAllNLD()
                            val project = projectDB.find { it.id == projectId }
                            val listOfProject = mutableListOf(project!!)
                            // call the export method from exportManager
                            taskFragmentViewModel.taskList.value?.let { tasks ->
                                exportManager.exportToCSV(listOfProject, tasks, requireContext(), requireActivity())
                            }
                        }
                    }
                }
            }
            true
        }

        val recyclerView = taskFragmentBinding.taskList

        // Observe changes in the taskList LiveData
        taskFragmentViewModel.taskList.observe(
            viewLifecycleOwner,
            Observer {
                // Set the adapter for the recyclerView with filtered tasks
                recyclerView.adapter = TaskAdapter(
                    taskFragmentViewModel,
                    requireContext(),
                    // Filter tasks based on projectId
                    it.filter { it.taskProjectId == projectId },
                    findNavController(),
                    projectId,
                    color,
                    lifecycleScope
                )

                Log.d("itemCount", recyclerView.adapter?.itemCount.toString())

                // Animate the floating action button whenever the task list is empty
                animator.animateFAB(it.filter { it.taskProjectId == projectId }.isEmpty(), taskFragmentBinding)
            }
        )

        taskFragmentBinding.floatingActionButton.setOnClickListener {

            // Create an AlertDialog.Builder instance
            val builder = AlertDialog.Builder(context)

            // Inflate the layout for the dialog
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_task, null)

            // Find the input views in the dialog layout
            val newTaskName = dialogLayout.findViewById<EditText>(R.id.input_task_name)
            val newTaskDescription =
                dialogLayout.findViewById<EditText>(R.id.input_task_description)

            with(builder) {
                setTitle("New Task")

                // Set the positive button click listener
                setPositiveButton("Ok") { dialog, which ->

                    // Get the entered task name and description
                    val newTaskNameString = newTaskName.text.toString()
                    val newTaskDescriptionString = newTaskDescription.text.toString()

                    // Create a new Task instance with project ID and color from arguments
                    val newTask = Task(taskProjectId = projectId, color = color.toString())

                    // set attributes of new task according to the user's input
                    if (newTaskNameString != "") {
                        newTask.name = newTaskNameString
                    }
                    if (newTaskDescriptionString != "") {
                        newTask.notes = newTaskDescriptionString
                    }

                    // Insert the new task into the task database
                    taskFragmentViewModel.insertTask(newTask) {

                        val database = getTaskDatabase(context)

                        // Launch a coroutine
                        lifecycleScope.launch {
                            // Perform the database operation within the coroutine
                            val taskList: List<Task> = withContext(Dispatchers.IO) {
                                database.taskDatabaseDao.getAllNLD()
                            }

                            // Get the newly added task
                            val task2Add = taskList.last()

                            Log.d("TaskFragment", currentUserId)

                            // Create a HashMap of attributes for firebaseManager
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

                            // Add the task to Firebase
                            firebaseManager.addTask(task2Add, attributes)

                            // Update number of tasks in project Object
                            val project = withContext(Dispatchers.IO) {
                                getProjectDatabase(context).projectDatabaseDao.getAllNLD()
                                    .find { it.id == projectId }
                            }

                            // get all tasks for the project
                            val tasks = withContext(Dispatchers.IO) {
                                getTaskDatabase(context).taskDatabaseDao.getAllNLD()
                                    .filter { it.taskProjectId == projectId }
                            }

                            // Calculate the total number of tasks
                            val tasksSize = tasks.size

                            // Update the project's number of tasks
                            project!!.numberOfTasks = tasksSize.toLong()

                            // Calculate the total elapsed time for all tasks
                            var totalTime = 0L

                            for (i in tasks) {
                                totalTime += i.elapsedTime
                            }

                            // Format the total elapsed time as a string
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

                            // Update the project's total time
                            project.totalTime = timeString

                            // Update the project in the project database
                            taskFragmentViewModel.updateProject(project)

                            // Create a map of updates for Firebase
                            val updates = mutableMapOf<String, Any>(
                                "numberOfTasks" to tasksSize,
                                "totalTime" to timeString
                            )

                            // Update the project changes in Firebase
                            firebaseManager.updateProjectChanges(project.id.toString(), updates)

                            // Display a toast message for task creation success
                            Toast.makeText(
                                context,
                                "$newTaskNameString created",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // Set the negative button click listener
                setNegativeButton("Cancel") { dialog, which ->

                    // Dismiss the dialog and display a toast message for task creation cancellation
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Task creation cancelled by user",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                // Set the custom layout for the dialog
                setView(dialogLayout)
                // show the dialog
                show()
            }
        }

        // get an instance of the task database and start listening to changes in firebase
        val database = getTaskDatabase(requireContext())
        taskSnapshotListener.startListening(database)
    }
}