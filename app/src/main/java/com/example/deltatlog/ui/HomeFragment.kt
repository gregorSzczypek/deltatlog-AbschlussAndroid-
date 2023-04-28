package com.example.deltatlog.ui

import Datasource
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.apicalls.adapter.ProjectAdapter
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserEmail = firebaseAuth.currentUser?.email
        // Set onClickListener on menu item logout
        binding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout ->
                firebaseAuth.signOut()
            }
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(
                    context,
                    "Successfully logged out user $currentUserEmail",
                    Toast.LENGTH_LONG
                ).show()
            }
            true
        }
    }
}