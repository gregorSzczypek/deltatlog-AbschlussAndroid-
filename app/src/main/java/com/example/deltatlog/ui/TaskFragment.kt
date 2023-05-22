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
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Task
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
        // BackButton Navigation in Toolbar
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToHomeFragment())
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
                    color
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

                            Log.d("ProjectFragment", currentUserId)

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

                            // Update number of tasks in the project in question
                            viewModel.taskList.observe(
                                viewLifecycleOwner
                            ) { it ->
                                val filteredTaskList = it.filter { it.taskProjectId == projectId }
                                val size = filteredTaskList.size.toLong()
                                Log.d("TaskFragment", "size: ${size.toString()}")
                                Log.d("TaskFragment", "projectid: ${projectId.toString()}")
                                viewModel.taskObserverTriggered = 1
                                viewModel.projectList.observe(
                                    viewLifecycleOwner
                                ) {
                                    if (viewModel.taskObserverTriggered == 1) {
                                        val project = it.find { it.id == projectId }
                                        Log.d(
                                            "TaskFragment",
                                            "inif nrtasks it: ${it.find { it.id == projectId }!!.numberOfTasks.toString()}"
                                        )
                                        Log.d("TaskFragment", "inif size: ${size}")
                                        project!!.numberOfTasks = size
                                        viewModel.updateProject(project)
                                        viewModel.taskObserverTriggered = 0
                                        Log.d("TaskFragment", "in if")
                                    }
                                    Log.d("TaskFragment", "projectid: triggered")
                                }
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
    }
}