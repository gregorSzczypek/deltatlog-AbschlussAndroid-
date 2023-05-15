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
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.viewModel

class TaskFragment : Fragment() {

    private val viewModel: viewModel by viewModels()
    private lateinit var binding: FragmentTaskBinding
    private var projectId: Long = 0
    private var color: String? = ""

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
        // BackButton Navigation in Toolbar
        binding.materialToolbar.setNavigationOnClickListener{
            findNavController().navigate(TaskFragmentDirections.actionProjectDetailFragmentToHomeFragment())
        }

        val recyclerView = binding.taskList

        viewModel.taskList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter = TaskAdapter(
                    viewModel,
                    requireContext(),
                    it.filter { it.taskProjectId == projectId },
                    findNavController(),
                    projectId,
                    color
                )
            }
        )

        binding.floatingActionButton.setOnClickListener{

            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_task, null)
            val newTaskName = dialogLayout.findViewById<EditText>(R.id.input_task_name)
            val newTaskDescription = dialogLayout.findViewById<EditText>(R.id.input_task_description)

            with(builder) {
                setTitle("New Task")
                setPositiveButton("Ok") {dialog, which ->
                    val newTaskNameString = newTaskName.text.toString()
                    val newTaskDescriptionString = newTaskDescription.text.toString()
                    val newTask = Task(taskProjectId = projectId, color = color.toString())

                    if (newTaskNameString != "") {
                        newTask.name = newTaskNameString
                    }
                    if (newTaskDescriptionString != "") {
                        newTask.notes = newTaskDescriptionString
                    }
                    viewModel.insertTask(newTask)
                    Toast.makeText(context, "$newTaskNameString created", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("Cancel") {dialog, which ->
                    dialog.dismiss()
                    Toast.makeText(context, "Task creation cancelled by user", Toast.LENGTH_SHORT).show()
                }
                setView(dialogLayout)
                show()
            }
        }
    }
}