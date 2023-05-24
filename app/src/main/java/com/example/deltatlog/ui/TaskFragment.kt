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
import androidx.room.withTransaction
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskFragment : Fragment() {

    private val viewModel: viewModel by viewModels()
    private lateinit var binding: FragmentTaskBinding
    private var projectId: Long = 0
    private var color: String? = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_task,
            container,
            false
        )

        // Hole die projectId aus den Argumenten
        projectId = requireArguments().getLong("projectId")
        Log.i("projectID", projectId.toString())
        color = requireArguments().getString("color")

        binding.taskList.setHasFixedSize(true) // set fixed size for recyclerview

        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser!!.uid
        val currentUserEmail = firebaseAuth.currentUser!!.email
        // BackButton Navigation in Toolbar
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToHomeFragment())
        }

        // Set onClickListener on menu item logout
        binding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    firebaseAuth.signOut()
                    findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
                    if (firebaseAuth.currentUser == null) {
                        viewModel.databaseDeleted = false
                        Toast.makeText(
                            context,
                            "Successfully logged out user $currentUserEmail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                R.id.export -> {

                }
            }
            true
        }

        val recyclerView = binding.taskList

        viewModel.taskList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter = TaskAdapter(
                    viewModel,
                    viewLifecycleOwner,
                    requireContext(),
                    it.filter { it.taskProjectId == projectId },
                    findNavController(),
                    projectId,
                    color,
                    lifecycleScope
                )
            }
        )

        binding.floatingActionButton.setOnClickListener {

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
                    viewModel.insertTask(newTask) {

                        val database = getTaskDatabase(context)

                        // Launch a coroutine
                        lifecycleScope.launch {
                            // Perform the database operation within the coroutine
                            val taskList: List<Task> = withContext(Dispatchers.IO) {
                                database.taskDatabaseDao.getAllNLD()
                            }

                            val task2Add = taskList.last()

                            Log.d("TaskFragment", currentUserId)

                            val firebaseItem2Add = hashMapOf(
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

                            db.collection("users").document(currentUserId)
                                .collection("tasks")
                                .document(task2Add.id.toString())
                                .set(firebaseItem2Add)
                                .addOnSuccessListener {
                                    Log.d(
                                        "firebase",
                                        "DocumentSnapshot successfully written!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "firebase",
                                        "Error writing document",
                                        e
                                    )
                                }

                            //TODO HERE UPDATE NR OF TASKS
                                val project = withContext(Dispatchers.IO) {
                                    getDatabase(context).projectDatabaseDao.getAllNLD()
                                        .find { it.id == projectId }
                                }
                                val tasksSize = withContext(Dispatchers.IO) {
                                    getTaskDatabase(context).taskDatabaseDao.getAllNLD()
                                        .filter { it.taskProjectId == projectId }.size
                                }
                                project!!.numberOfTasks = tasksSize.toLong()
                                viewModel.updateProject(project)

                                // TODO Update project changes in firebase
                                db.collection("users").document(currentUserId)
                                    .collection("projects")
                                    .document(projectId.toString())
                                    .update("numberOfTasks", tasksSize)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "update",
                                            "DocumentSnapshot successfully updated!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "update",
                                            "Error updating document",
                                            e
                                        )
                                    }

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
        val db = Firebase.firestore
        val projectCollection = db.collection("users").document(currentUserId)
            .collection("tasks")
        val database = getTaskDatabase(requireContext())

        // TODO Snapshot listener for firebase changes

        projectCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            val tasks = mutableListOf<Task>()

            for (doc in snapshot?.documents ?: emptyList()) {
                val id = doc.id.toLong()
                val taskProjectId = doc.getLong("taskProjectId")?: 0
                val name = doc.getString("name") ?: ""
                val color = doc.getString("color") ?: ""
                val date = doc.getString("date") ?: ""
                val duration = doc.getString("duration") ?: ""
                val description = doc.getString("description") ?: ""
                val notes = doc.getString("notes") ?: ""
                val elapsedTime = doc.getLong("elapsedTime")?: 0

                val task = Task(
                    id,
                    taskProjectId,
                    name,
                    color,
                    date,
                    duration,
                    description,
                    notes,
                    elapsedTime,
                )
                tasks.add(task)
            }

            // Update the local Room database
            lifecycleScope.launch {
                // Run the database operation within a transaction
                database.withTransaction {
                    // Retrieve the current projects from the database
                    val currentTasks = database.taskDatabaseDao.getAllNLD()

                    // Compare the projects from Firestore with the current projects
                    val tasksToDelete = currentTasks.filter { it !in tasks }
                    val tasksToInsert = tasks.filter { it !in currentTasks}

                    // Delete projects that are no longer present in Firestore
                    database.taskDatabaseDao.deleteTasks(tasksToDelete)

                    // Insert new projects from Firestore
                    database.taskDatabaseDao.insertAll(tasksToInsert)
                }
            }
        }
    }
}