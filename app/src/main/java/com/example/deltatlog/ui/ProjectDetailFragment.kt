package com.example.deltatlog.ui

import Datasource
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.apicalls.adapter.ProjectAdapter
import com.example.apicalls.adapter.TaskAdapter
import com.example.apicalls.data.datamodels.Project
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentProjectDetailBinding


class ProjectDetailFragment : Fragment() {

    private lateinit var binding: FragmentProjectDetailBinding
    private var projectId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_project_detail,
            container,
            false
        )

        // Hole die contactId aus den Argumenten
        projectId = requireArguments().getLong("projectId")
        Log.i("conID", projectId.toString())

//        val project = viewModel.projectList.value?.find { it.id == projectId.toInt() } Später mit live data!!
        val project = Datasource().loadProjects().find { it.id == projectId }

        val tasks = Datasource().loadTasks(project!!) // Wir holen unsere Liste mit unseren Objekten aus Datasource
        binding.taskList.adapter = TaskAdapter(tasks) // Wir übergeben die RV unseren Adapter

        binding.taskList.setHasFixedSize(true) // optional - legt fest dass das item layout sich nicht verändert
        // Dies verhindert eine ständige neu Berechnung der Layout Dimensionen und macht die RV schlanker und performanter


        // Inflate the layout for this fragment
        return binding.root
    }
}