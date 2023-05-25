package com.example.deltatlog.ui

import ProjectAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ProjectFragment : Fragment() {

    // ViewModels, bindings, and Firebase components
    private val ProjectFragmentViewModel: viewModel by viewModels()
    private lateinit var ProjectFragmentBinding: FragmentProjectBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout and initialize the binding object
        ProjectFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_project,
            container,
            false
        )
        // Set the lifecycle owner for data binding
        ProjectFragmentBinding.lifecycleOwner = this.viewLifecycleOwner
        // set fixed size for recycler view
        ProjectFragmentBinding.projectList.setHasFixedSize(true)

        // Return the root view of the fragment
        return ProjectFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserEmail = firebaseAuth.currentUser?.email
        var currentUserId = firebaseAuth.currentUser!!.uid


        // Set onClickListener on menu item logout
        ProjectFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            destroySnapListener()
            when (it.itemId) {
                R.id.logout -> {
                    firebaseAuth.signOut()
                    findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
                    if (firebaseAuth.currentUser == null) {
                        ProjectFragmentViewModel.databaseDeleted = false
                        Toast.makeText(
                            context,
                            "Successfully logged out user $currentUserEmail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                R.id.export -> {
                    ProjectFragmentViewModel.projectList.value?.let { projects ->
                        exportToCSV(projects)
                    }
                }
            }
            true
        }

        val recyclerView = ProjectFragmentBinding.projectList

        ProjectFragmentViewModel.projectList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter =
                    ProjectAdapter(ProjectFragmentViewModel, requireContext(), it, lifecycleScope)
            }
        )

        // Handle click event for adding a new project
        ProjectFragmentBinding.floatingActionButton.setOnClickListener {

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

                    if (newCompanyNameString != "") {
                        ProjectFragmentViewModel.loadLogo(newCompanyNameString) {
                            Log.d("ProjectFragment", "(5) Here updating logourl")
                            Log.d("ProjectFragment", ProjectFragmentViewModel.logoLiveData.value!!.logo)

                            newProject.logoUrl = ProjectFragmentViewModel.logoLiveData.value!!.logo

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

                            ProjectFragmentViewModel.insertProject(newProject) {

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

                        ProjectFragmentViewModel.insertProject(newProject) {

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
        currentUserId = firebaseAuth.currentUser!!.uid
        val projectCollection = db.collection("users").document(currentUserId)
            .collection("projects")

        // TODO Snapshot listener for firebase changes

        val database = getDatabase(requireContext())
        val taskDatabase = getTaskDatabase(requireContext())

        if (ProjectFragmentViewModel.databaseDeleted == false) {
            lifecycleScope.launch {
                database.projectDatabaseDao.deleteAllProjects()
                taskDatabase.taskDatabaseDao.deleteAllTasks()
            }
            ProjectFragmentViewModel.databaseDeleted = true
        }

        snapshotListener = projectCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle error
                return@addSnapshotListener
            }

            val projects = mutableListOf<Project>()

            for (doc in snapshot?.documents ?: emptyList()) {
                val id = doc.getLong("id") ?: 0
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
    fun destroySnapListener() {
        snapshotListener?.remove()
    }

    private fun convertToCSV(projects: List<Project>): String {
        val header = "ID,Name,NameCustomer,CompanyName,Homepage,LogoUrl,Image,Date,Description,Color,NumberOfTasks,TotalTime\n"
        val rows = projects.joinToString("\n") { project ->
            "${project.id},${project.name},${project.nameCustomer},${project.companyName},${project.homepage}," +
                    "${project.logoUrl},${project.image},${project.date},${project.description},${project.color}," +
                    "${project.numberOfTasks},${project.totalTime}"
        }
        return header + rows
    }

    private fun exportToCSV(projects: List<Project>) {
        val csvData = convertToCSV(projects)

        val filename = "project_database.csv"
        val file = File(requireContext().externalCacheDir, filename)

        try {
            FileWriter(file).use { writer ->
                writer.append(csvData)
            }
            Toast.makeText(requireContext(), "CSV file exported", Toast.LENGTH_SHORT).show()
            sendEmail(file)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Failed to export CSV file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun sendEmail(file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Project Database CSV")
        intent.putExtra(Intent.EXTRA_TEXT, "Please find attached the project database in CSV format.")
        val uri = FileProvider.getUriForFile(requireContext(), "com.example.deltatlog.fileprovider", file)
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        startActivity(Intent.createChooser(intent, "Send Email"))
    }
}