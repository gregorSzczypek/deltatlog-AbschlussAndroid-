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
import androidx.room.withTransaction
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.databinding.FragmentProjectBinding
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectFragment : Fragment() {

    private val viewModel: viewModel by viewModels()
    private lateinit var binding: FragmentProjectBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

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
        binding.projectList.setHasFixedSize(true) // set fixed size for recycler view

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserEmail = firebaseAuth.currentUser?.email
        val currentUserId = firebaseAuth.currentUser!!.uid


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
            Observer {
                recyclerView.adapter =
                    ProjectAdapter(viewModel, requireContext(), it, lifecycleScope)
            }
        )
        binding.floatingActionButton.setOnClickListener {

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
                    val newProject = Project()

                    if (newCompanyNameString != "") {
                        viewModel.loadLogo(newCompanyNameString) {
                            Log.d("ProjectFragment", "(5) Here updating logourl")
                            Log.d("ProjectFragment", viewModel.logoLiveData.value!!.logo)

                            newProject.logoUrl = viewModel.logoLiveData.value!!.logo

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

                            viewModel.insertProject(newProject) {

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
                    } else {

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

                        viewModel.insertProject(newProject) {

                            val database = getDatabase(context)

                            // Launch a coroutine
                            lifecycleScope.launch {
                                // Perform the database operation within the coroutine
                                val projectList: List<Project> = withContext(Dispatchers.IO) {
                                    database.projectDatabaseDao.getAllNLD()
                                }

                                val project2Add = projectList.last()

                                Log.d("ProjectFragment", project2Add.id.toString())
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
        // here firebase stuff
        val db = Firebase.firestore
        val projectCollection = db.collection("users").document(currentUserId)
            .collection("projects")

        // TODO Snapshot listener for firebase changes

        val database = getDatabase(requireContext())
        val taskDatabase = getTaskDatabase(requireContext())

        if (viewModel.databaseDeleted == false) {
            lifecycleScope.launch {
                database.projectDatabaseDao.deleteAllProjects()
                taskDatabase.taskDatabaseDao.deleteAllTasks()
            }
            viewModel.databaseDeleted = true
        }

        projectCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            val projects = mutableListOf<Project>()

            for (doc in snapshot?.documents ?: emptyList()) {
                val id = doc.id.toLong()
                val name = doc.getString("name") ?: ""
                val nameCustomer = doc.getString("nameCustomer") ?: ""
                val companyName = doc.getString("companyName") ?: ""
                val homepage = doc.getString("homepage") ?: ""
                val logoUrl = doc.getString("logoUrl") ?: ""
                val image = doc.getLong("image")?.toInt() ?: 0
                val date = doc.getString("date") ?: ""
                val description = doc.getString("description") ?: ""
                val color = doc.getString("color") ?: ""
                val numberOfTasks = doc.getLong("numberOfTasks")?.toInt() ?: 0
                val totalTime = doc.getString("totalTime") ?: ""

                val project = Project(
                    id,
                    name,
                    nameCustomer,
                    companyName,
                    homepage,
                    logoUrl,
                    image.toInt(),
                    date,
                    description,
                    color,
                    numberOfTasks.toLong(),
                    totalTime
                )
                projects.add(project)
            }

            // Update the local Room database
            lifecycleScope.launch {
                // Run the database operation within a transaction
                database.withTransaction {
                    // Retrieve the current projects from the database
                    val currentProjects = database.projectDatabaseDao.getAllNLD()

                    // Compare the projects from Firestore with the current projects
                    val projectsToDelete = currentProjects.filter { it !in projects }
                    val projectsToInsert = projects.filter { it !in currentProjects }

                    // Delete projects that are no longer present in Firestore
                    database.projectDatabaseDao.deleteProjects(projectsToDelete)

                    // Insert new projects from Firestore
                    database.projectDatabaseDao.insertAll(projectsToInsert)
                }
            }
        }
    }
}