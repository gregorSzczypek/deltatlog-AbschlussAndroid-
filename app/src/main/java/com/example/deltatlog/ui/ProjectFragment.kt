package com.example.deltatlog.ui

import ProjectAdapter
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
import com.example.deltatlog.ExportManager
import com.example.deltatlog.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.databinding.FragmentProjectBinding
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectFragment : Fragment() {

    // ViewModels, bindings, and Firebase components
    private val projectFragmentViewModel: viewModel by viewModels()
    private lateinit var projectFragmentBinding: FragmentProjectBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firebaseManager = FirebaseManager()
    private val db = Firebase.firestore
    private val exportManager = ExportManager()
    private lateinit var projectSnapshotListener: ProjectSnapshotListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout and initialize the binding object
        projectFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_project,
            container,
            false
        )
        // Set the lifecycle owner for data binding
        projectFragmentBinding.lifecycleOwner = this.viewLifecycleOwner
        // set fixed size for recycler view
        projectFragmentBinding.projectList.setHasFixedSize(true)

        // Return the root view of the fragment
        return projectFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserEmail = firebaseAuth.currentUser?.email
        var currentUserId = firebaseAuth.currentUser!!.uid
        projectSnapshotListener = ProjectSnapshotListener(this, firebaseAuth.currentUser!!.uid)


        // Set onClickListener on menu item logout
        projectFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    projectSnapshotListener.stopListening()
                    firebaseManager.logOut(
                        firebaseAuth,
                        projectFragmentViewModel,
                        currentUserEmail!!,
                        requireContext()
                    )
                    findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
                }

                R.id.export -> {
                    projectFragmentViewModel.projectList.value?.let { projects ->
                        exportManager.exportProjectsToCSV(projects, requireContext())
                    }
                }
            }
            true
        }

        val recyclerView = projectFragmentBinding.projectList

        projectFragmentViewModel.projectList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter =
                    ProjectAdapter(projectFragmentViewModel, requireContext(), it, lifecycleScope)
            }
        )

        // Handle click event for adding a new project
        projectFragmentBinding.floatingActionButton.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_project, null)
            val newProjectName = dialogLayout.findViewById<EditText>(R.id.input_project_name)
            val newCustomerName =
                dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
            val newDescription =
                dialogLayout.findViewById<EditText>(R.id.input_project_description)
            val newCompanyName = dialogLayout.findViewById<EditText>(R.id.input_company_name)

            with(builder) {
                setTitle("New Project")
                setPositiveButton("Ok") { dialog, which ->
                    val newProjectNameString = newProjectName.text.toString()
                    val newCustomerNameString = newCustomerName.text.toString()
                    val newDescriptionString = newDescription.text.toString()
                    val newCompanyNameString = newCompanyName.text.toString()
                    // create new Project instance
                    val newProject = Project()

                    projectFragmentViewModel.loadLogo(newCompanyNameString) {
                        if (newCompanyNameString != "") {
                            Log.d("ProjectFragment", "(5) Here updating logourl")
                            Log.d(
                                "ProjectFragment",
                                projectFragmentViewModel.logoLiveData.value!!.logo
                            )

                            newProject.logoUrl =
                                projectFragmentViewModel.logoLiveData.value!!.logo
                        }

                        if (newProjectNameString != "") {
                            newProject.name = newProjectNameString
                        }
                        if (newCustomerNameString != "") {
                            newProject.nameCustomer = newCustomerNameString
                        }
                        if (newDescriptionString != "") {
                            newProject.description = newDescriptionString
                        }
                        if (newCompanyNameString != "") {
                            newProject.companyName = newCompanyNameString
                        }
                        Log.d("ProjectFragment", newProject.logoUrl)
                        Log.d("userID", currentUserId)

                        projectFragmentViewModel.insertProject(newProject) {

                            val database = getDatabase(context)

                            // Launch a coroutine
                            lifecycleScope.launch {
                                // Perform the database operation within the coroutine
                                val projectList: List<Project> =
                                    withContext(Dispatchers.IO) {
                                        database.projectDatabaseDao.getAllNLD()
                                    }

                                val project2Add = projectList.last()

                                Log.d("ProjectFragment", currentUserEmail!!)
                                Log.d("ProjectFragment", currentUserId)

                                val firebaseItem2Add = hashMapOf(
                                    "id" to project2Add.id,
                                    "name" to project2Add.name,
                                    "date" to project2Add.date,
                                    "nameCustomer" to project2Add.nameCustomer,
                                    "companyName" to project2Add.companyName,
                                    "homepage" to project2Add.homepage,
                                    "logoUrl" to project2Add.logoUrl,
                                    "image" to project2Add.image,
                                    "date" to project2Add.date,
                                    "description" to project2Add.description,
                                    "color" to project2Add.color,
                                    "numberOfTasks" to project2Add.numberOfTasks,
                                    "totalTime" to project2Add.totalTime
                                )

                                Log.d("firebasePID", project2Add.id.toString())

                                db.collection("users").document(currentUserId)
                                    .collection("projects")
                                    .document(project2Add.id.toString())
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

                                Toast.makeText(
                                    context,
                                    "$newProjectNameString created",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Project creation cancelled by user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setView(dialogLayout)
                show()
            }
        }

        val projectDatabase = getDatabase(requireContext())
        projectSnapshotListener.startListening(projectDatabase)
    }
}