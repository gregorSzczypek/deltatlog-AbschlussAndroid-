package com.example.deltatlog.ui

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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.viewModel
import com.example.deltatlog.databinding.FragmentLoginBinding
import com.example.deltatlog.databinding.FragmentTimerBinding
import java.util.Locale


class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private val viewModel: viewModel by viewModels()
    private var taskId: Long = 0
    private var projectId: Long = 0

    var seconds = 0
    var isRunning = true
    var oldTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            taskId = it.getLong("taskId")
            projectId = it.getLong("projectId")
        }
        Log.i("TimerFragment", "Task ID: $taskId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timer,
            container,
            false
        )
        // damit LiveData automatisch observed wird vom layout
        binding.lifecycleOwner = this.viewLifecycleOwner
        runTimer()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.btnStop.setOnClickListener {

            isRunning = false

            viewModel.taskList.observe(
                viewLifecycleOwner,
                Observer {
                    val currentTask = it.filter { it.id == taskId && it.taskProjectId == projectId }[0]
                    val newTimeSeconds = seconds + currentTask.elapsedTime
                    currentTask.elapsedTime = newTimeSeconds

                    val hours = newTimeSeconds / 3600
                    val minutes = (newTimeSeconds % 3600) / 60
                    val sec = newTimeSeconds % 60
                    val timeString = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)
                    currentTask.duration = timeString

                    viewModel.updateTask(currentTask)
                }
            )

            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val sec = seconds % 60
            var time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)
            Toast.makeText(context, "Session time: $time", Toast.LENGTH_SHORT).show()

            findNavController().navigate(
                TimerFragmentDirections.actionTimerFragmentToProjectDetailFragment(
                    projectId
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
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val sec = seconds % 60
                val time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, sec)
                timerTextView.text = time

                // Stopp handler for 1 second
                handler.postDelayed(r, 1000)
            }
        }
    }
}
