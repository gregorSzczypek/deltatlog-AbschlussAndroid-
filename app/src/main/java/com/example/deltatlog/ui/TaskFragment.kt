package com.example.deltatlog.ui

import Datasource
import TaskAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
//import com.example.apicalls.adapter.TaskAdapter
import com.example.apicalls.adapter.TaskAttrAdapter
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentTaskBinding
import com.example.deltatlog.databinding.ListItemTaskBinding


class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding

    private var projectId: Long = 0

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

        // Hole die contactId aus den Argumenten
        projectId = requireArguments().getLong("projectId")
        Log.i("conID", projectId.toString())

//        val project = viewModel.projectList.value?.find { it.id == projectId.toInt() } Später mit live data!!
        val project = Datasource().loadProjects().find { it.id == projectId }

//        val tasks = Datasource().loadTasks(project!!) // Wir holen unsere Liste mit unseren Objekten aus Datasource
//        binding.taskList.adapter = TaskAdapter(requireContext(), tasks) // Wir übergeben die RV unseren Adapter

        binding.taskList.setHasFixedSize(true) // optional - legt fest dass das item layout sich nicht verändert
        // Dies verhindert eine ständige neu Berechnung der Layout Dimensionen und macht die RV schlanker und performanter

//        val helperTask: SnapHelper = LinearSnapHelper()
//        helperTask.attachToRecyclerView(binding.taskList)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // BackButton Navigation in Toolbar
        binding.materialToolbar.setNavigationOnClickListener{
            findNavController().navigateUp()
        }
    }
}