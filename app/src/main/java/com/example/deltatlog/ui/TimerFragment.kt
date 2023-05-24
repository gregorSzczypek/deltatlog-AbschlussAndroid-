package com.example.deltatlog.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentTimerBinding
import com.example.deltatlog.viewModel
import java.util.Locale


class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private val viewModel: viewModel by viewModels()
    private val firebaseManager = FirebaseManager()
    private var taskId: Long = 0
    private var projectId: Long = 0
    private var taskName: String? = ""
    private var color: String? = ""

    var seconds = 0
    var isRunning = true

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            taskId = it.getLong("taskId")
            projectId = it.getLong("projectId")
            taskName = it.getString("taskName")
            color = it.getString("color")
        }
        Log.i("TimerFragment", "Task ID: $taskId")
        Log.i("TimerFragment", "Project ID: ${projectId}")
        Log.i("TimerFragment", "Task Name: $taskName")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using DataBindingUtil
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timer,
            container,
            false
        )
        // Set text colors based on the provided color
        binding.tvTimer.setTextColor(Color.parseColor(color))
        binding.tvTaskName.setTextColor(Color.parseColor(color))

        // To automatically observe LiveData in the layout
        binding.lifecycleOwner = this.viewLifecycleOwner

        // Restore the saved instance state if available
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds", 0)
        }

        // start the timer
        runTimer()

        // Return the root view of the inflated layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Set the task name in the text view
        binding.tvTaskName.text = taskName

        // Handle the stop button click
        binding.btnStop.setOnClickListener {

            isRunning = false

            viewModel.taskList.observe(
                viewLifecycleOwner,
                Observer {
                    // Retrieve the current task from the list
                    val currentTask =
                        it.filter { it.id == taskId && it.taskProjectId == projectId }[0]

                    // Calculate the new elapsed time
                    val newTimeSeconds = seconds + currentTask.elapsedTime
                    currentTask.elapsedTime = newTimeSeconds

                    // Format the time as a string
                    val hours = newTimeSeconds / 3600
                    val minutes = (newTimeSeconds % 3600) / 60
                    val sec = newTimeSeconds % 60
                    val timeString =
                        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)

                    // Update the duration and elapsed time of the task in the ViewModel
                    currentTask.duration = timeString
                    viewModel.updateTask(currentTask)

                    // Prepare the updates to be sent to Firebase
                    val updates = mapOf(
                        "duration" to currentTask.duration,
                        "elapsedTime" to currentTask.elapsedTime
                    )
                    // Update the task changes in Firebase via the firebaseManager
                    firebaseManager.updateTaskChanges(taskId = taskId.toString(), updates = updates)
                }
            )

            // Calculate the time as hours, minutes, and seconds
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val sec = seconds % 60
            var time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)

            // Display a toast with the session time
            Toast.makeText(context, "Session time: $time", Toast.LENGTH_SHORT).show()

            findNavController().navigate(
                TimerFragmentDirections.actionTimerFragmentToProjectDetailFragment(
                    projectId,
                    color
                )
            )
        }
    }

    private fun runTimer() {

        val timerTextView = binding.tvTimer
        val handler = Handler()
        val r = Runnable { runTimer() }

        handler.post {
            run {
                if (isRunning) {
                    seconds++
                }
                // Calculate the time as hours, minutes, and seconds
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val sec = seconds % 60
                // Format the time as a string and set it in the text view
                val time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)
                timerTextView.text = time

                // Schedule the next execution of the timer after 1 second
                handler.postDelayed(r, 1000)
            }
        }
    }
}
