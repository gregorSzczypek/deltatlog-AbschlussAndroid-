package com.example.deltatlog.ui

import Datasource
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.apicalls.adapter.ProjectAdapter
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        val projects = Datasource().loadProjects() // load projects from Datasource (gor testing only)
        binding.projectList.adapter = ProjectAdapter(projects) // attach adapter to recycler view

        binding.projectList.setHasFixedSize(true) // set fixed size for recycler view
        // performance


        // Inflate the layout for this fragment
        return binding.root
    }
}