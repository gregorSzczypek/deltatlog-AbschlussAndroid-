package com.example.deltatlog.ui

import ProjectAdapter
import android.app.AlertDialog
import android.os.Bundle
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
import com.example.deltatlog.viewModel
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.databinding.FragmentProjectBinding
import com.google.firebase.auth.FirebaseAuth


class ProjectFragment : Fragment() {

    private val viewModel: viewModel by viewModels()
    private lateinit var binding: FragmentProjectBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_project,
            container,
            false
        )

        // observe livedata
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.loadProjectData() // load projects into DB
        binding.projectList.setHasFixedSize(true) // set fixed size for recycler view

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
            findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(
                    context,
                    "Successfully logged out user $currentUserEmail",
                    Toast.LENGTH_LONG
                ).show()
            }
            true
        }

        val recyclerView = binding.projectList

        viewModel.projectList.observe(
            viewLifecycleOwner,
            Observer {recyclerView.adapter = ProjectAdapter(viewModel, requireContext(), it)
            }
        )

        binding.floatingActionButton.setOnClickListener{

            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_project, null)
            val newProjectName = dialogLayout.findViewById<EditText>(R.id.input_project_name)
            val newCustomerName = dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
            val newDescription = dialogLayout.findViewById<EditText>(R.id.input_project_description)
            val newHomepage = dialogLayout.findViewById<EditText>(R.id.input_company_homepage)

            with(builder) {
                setTitle("New Project")
                setPositiveButton("Ok") {dialog, which ->
                    val newProjectNameString = newProjectName.text.toString()
                    val newCustomerNameString = newCustomerName.text.toString()
                    val newDescriptionString = newDescription.text.toString()
                    val newHomepageString = newHomepage.text.toString()
                    val newProject = Project()

                    if (newProjectNameString != "") {
                        newProject.name = newProjectNameString
                    }
                    if (newCustomerNameString != "") {
                        newProject.nameCustomer = newCustomerNameString
                    }
                    if (newDescriptionString != "") {
                        newProject.description = newDescriptionString
                    }
                    if (newHomepageString != "") {
                        newProject.homepage = newHomepageString
                    }

                    newProject.logoUrl = "https://logo.clearbit.com/${newProject.homepage}"
                    viewModel.insertProject(newProject)
                    Toast.makeText(context, "$newProjectNameString created", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("Cancel") {dialog, which ->
                    dialog.dismiss()
                    Toast.makeText(context, "Project creation cancelled by user", Toast.LENGTH_SHORT).show()
                }
                setView(dialogLayout)
                show()
            }
        }
    }
}