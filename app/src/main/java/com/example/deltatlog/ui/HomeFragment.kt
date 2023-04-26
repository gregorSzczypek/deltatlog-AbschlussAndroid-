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

        val projects = Datasource().loadProjects() // Wir holen unsere Liste mit unseren Objekten aus Datasource
        binding.projectList.adapter = ProjectAdapter(projects) // Wir übergeben der RV unseren Adapter

        binding.projectList.setHasFixedSize(true) // optional - legt fest dass das item layout sich nicht verändert
        // Dies verhindert eine ständige neu Berechnung der Layout Dimensionen und macht die RV schlanker und performanter


        // Inflate the layout for this fragment
        return binding.root
    }
}