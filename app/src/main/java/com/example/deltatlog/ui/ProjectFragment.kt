package com.example.deltatlog.ui

import ProjectAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.util.ExportManager
import com.example.deltatlog.util.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getProjectDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.databinding.FragmentProjectBinding
import com.example.deltatlog.util.ProjectSnapshotListener
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectFragment : Fragment() {

    // ViewModels, bindings, and Firebase components, exportmanager instance
    private val projectFragmentViewModel: viewModel by viewModels()
    private lateinit var projectFragmentBinding: FragmentProjectBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firebaseManager = FirebaseManager()
    private val exportManager = ExportManager()
    private lateinit var projectSnapshotListener: ProjectSnapshotListener
    private lateinit var swellAnimation: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        Log.d("deleteDB", projectFragmentViewModel.databaseDeleted.toString())

        swellAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.swell_animation)
        // check if database has been deleted
        if (!projectFragmentViewModel.databaseDeleted)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    // delete all projects from the database
                    getProjectDatabase(requireContext()).projectDatabaseDao.deleteAllProjects()
                    // delete all tasks from the database
                    //getTaskDatabase(requireContext()).taskDatabaseDao.deleteAllTasks()
                }
                // Set check variable to true after the deletion
                projectFragmentViewModel.databaseDeleted = true
            }

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        // get instance of current user email adress
        val currentUserEmail = firebaseAuth.currentUser?.email
        // set up firebase snapshotlistener
        projectSnapshotListener = ProjectSnapshotListener(this, firebaseAuth.currentUser!!.uid)


        // Set onClickListener on menu item logout
        projectFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // destroy snapshot listener
                            projectSnapshotListener.stopListening()
                            // logout user routine
                            firebaseManager.logOut(firebaseAuth, currentUserEmail!!, requireContext())
                            // set check variable for database deletion to false
                            projectFragmentViewModel.databaseDeleted = false
                            findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
                        }
                        .setNegativeButton("No", null)
                        .create()

                    alertDialog.show()
                }

                R.id.export -> {
                    // start a coroutine
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            // within the coroutine get all tasks and save is to list
                            val tasks =
                                getTaskDatabase(requireContext()).taskDatabaseDao.getAllNLD()
                            Log.d("tasks", tasks.first().name)
                            // within the coroutine get all projects and save those to a list
                            val projects =
                                getProjectDatabase(requireContext()).projectDatabaseDao.getAllNLD()
                            Log.d("tasks", projects.first().name)
                            // call the export routine from export manager class
                            exportManager.exportToCSV(projects, tasks, requireContext())
                        }
                    }
                }

                R.id.deleteAccount -> {
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Account Deletion")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes") { _, _ ->
                            firebaseManager.deleteAccount(firebaseAuth, currentUserEmail!!, requireContext())
                        }
                        .setNegativeButton("No", null)
                        .create()

                    alertDialog.show()
                }
            }
            true
        }
        // Initialize the recyclerView variable with the projectList from projectFragmentBinding
        val recyclerView = projectFragmentBinding.projectList

        // Observe changes in the projectList LiveData from the projectFragmentViewModel
        projectFragmentViewModel.projectList.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter =
                    ProjectAdapter(projectFragmentViewModel, requireContext(), it, lifecycleScope)
                Log.d("itemCount", recyclerView.adapter?.itemCount.toString())
                // Call the animateFAB function, passing a boolean value indicating if the observed data (it) is empty
                // there is a hint text shown and the button is animated as long it.isEmpty() == true
                animateFAB(it.isEmpty())
            }
        )

        // Handle click event for adding a new project
        projectFragmentBinding.floatingActionButton.setOnClickListener {

            // Create an AlertDialog.Builder object
            val builder = AlertDialog.Builder(context)

            // Inflate the layout for the dialog
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_project, null)

            // Find the EditText views from the dialog layout
            val newProjectName = dialogLayout.findViewById<EditText>(R.id.input_project_name)
            val newCustomerName =
                dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
            val newDescription =
                dialogLayout.findViewById<EditText>(R.id.input_project_description)
            val newCompanyName = dialogLayout.findViewById<EditText>(R.id.input_company_name)
            val newEstimatedTime = dialogLayout.findViewById<EditText>(R.id.input_estimated_time)

            // Set properties for the AlertDialog.Builder
            with(builder) {
                setTitle("New Project")

                // Set the positive button click listener
                setPositiveButton("Ok") { dialog, which ->
                    val newProjectNameString = newProjectName.text.toString()
                    val newCustomerNameString = newCustomerName.text.toString()
                    val newDescriptionString = newDescription.text.toString()
                    val newCompanyNameString = newCompanyName.text.toString()
                    val newEstimatedTimeString = newEstimatedTime.text.toString()

                    // create new Project instance
                    val newProject = Project()

                    // Load the logo for the new project
                    projectFragmentViewModel.loadLogo(newCompanyNameString) {
                        // Update the logoUrl of the new project if a logo is available
                        if (newCompanyNameString != "") {
                            Log.d("ProjectFragment", "(5) Here updating logourl")
                            Log.d(
                                "ProjectFragment",
                                projectFragmentViewModel.logoLiveData.value!!.logo
                            )

                            newProject.logoUrl =
                                projectFragmentViewModel.logoLiveData.value!!.logo
                        }

                        // Set the values of the new project based on the entered data
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
                        if (newEstimatedTimeString != "") {
                            newProject.estimatedTime = newEstimatedTimeString.toInt()
                        }

                        // Generate a random color for the new project
                        val colorString = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                projectFragmentViewModel.colors.random()
                            )
                        ).substring(2).uppercase()

                        // assign the generated color to the new project instance
                        newProject.color = colorString

                        Log.d("ProjectFragment", newProject.color)

                        // Insert the new project into the database
                        projectFragmentViewModel.insertProject(newProject) {

                            val database = getProjectDatabase(context)

                            // Launch a coroutine to perform database operations
                            lifecycleScope.launch {

                                // Perform the database operation within the coroutine
                                val projectList: List<Project> =
                                    withContext(Dispatchers.IO) {
                                        database.projectDatabaseDao.getAllNLD()
                                    }

                                // find the last project to add to firebase
                                val project2Add = projectList.last()

                                Log.d("ProjectFragment", currentUserEmail!!)

                                // create a hashmap of attributes for firebase addProject method
                                val attributes = hashMapOf<String, Any>(
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

                                // call addProject method from firebase class
                                firebaseManager.addProject(project2Add, attributes)

                                // show toast with confirmation
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
                // set negative button and cancel the creation and show toast for confirmation of cancellation
                setNegativeButton("Cancel") { dialog, which ->

                    // close dialog on negative button
                    dialog.dismiss()

                    // show canellation toast
                    Toast.makeText(
                        context,
                        "Project creation cancelled by user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // Set the custom layout for the dialog
                setView(dialogLayout)
                // show the dialog
                show()
            }
        }

        val projectDatabase = getProjectDatabase(requireContext())
        projectSnapshotListener.startListening(projectDatabase)
    }

    private fun animateFAB(isEmpty: Boolean) {
        val fab = projectFragmentBinding.floatingActionButton

        // Scale animation
        if (isEmpty) {
            // Create scale animation
            val scaleAnimation = ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            // Rotate animation
            val rotate = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 2000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
                interpolator = AccelerateDecelerateInterpolator()
            }

            // Create an animation set and add scale and rotate animations to it
            val animationSet = AnimationSet(true).apply {
                addAnimation(scaleAnimation)
                addAnimation(rotate)
            }
            // start the animation set
            fab.startAnimation(animationSet)

            // Show hint arrow and text
            projectFragmentBinding.hintArrow.visibility = View.VISIBLE
            projectFragmentBinding.hintText.visibility = View.VISIBLE
        } else {
            // clear the animation
            fab.clearAnimation()

            // hide hint aroow and text
            projectFragmentBinding.hintArrow.visibility = View.INVISIBLE
            projectFragmentBinding.hintText.visibility = View.INVISIBLE
        }
    }
}