package com.example.deltatlog.ui

import ProjectAdapter
import android.animation.ObjectAnimator
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
import androidx.recyclerview.widget.RecyclerView
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

    // ViewModels, bindings, and Firebase components
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

        if (!projectFragmentViewModel.databaseDeleted)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    getProjectDatabase(requireContext()).projectDatabaseDao.deleteAllProjects()
//                    getTaskDatabase(requireContext()).taskDatabaseDao.deleteAllTasks()
                }
                projectFragmentViewModel.databaseDeleted = true
            }

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserEmail = firebaseAuth.currentUser?.email
        projectSnapshotListener = ProjectSnapshotListener(this, firebaseAuth.currentUser!!.uid)


        // Set onClickListener on menu item logout
        projectFragmentBinding.materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    projectSnapshotListener.stopListening()
                    firebaseManager.logOut(
                        firebaseAuth,
                        currentUserEmail!!,
                        requireContext()
                    )
                    projectFragmentViewModel.databaseDeleted = false
                    findNavController().navigate(ProjectFragmentDirections.actionHomeFragmentToLoginFragment())
                }

                R.id.export -> {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val tasks =
                                getTaskDatabase(requireContext()).taskDatabaseDao.getAllNLD()
                            Log.d("tasks", tasks.first().name)
                            val projects =
                                getProjectDatabase(requireContext()).projectDatabaseDao.getAllNLD()
                            Log.d("tasks", projects.first().name)
                            exportManager.exportAllToCSV(projects, tasks, requireContext())
                        }
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
                Log.d("itemCount", recyclerView.adapter?.itemCount.toString())
                animateFAB(it.isEmpty())
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
            val newEstimatedTime = dialogLayout.findViewById<EditText>(R.id.input_estimated_time)

            with(builder) {
                setTitle("New Project")
                setPositiveButton("Ok") { dialog, which ->
                    val newProjectNameString = newProjectName.text.toString()
                    val newCustomerNameString = newCustomerName.text.toString()
                    val newDescriptionString = newDescription.text.toString()
                    val newCompanyNameString = newCompanyName.text.toString()
                    val newEstimatedTimeString = newEstimatedTime.text.toString()

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
                        if (newEstimatedTimeString != "") {
                            newProject.estimatedTime = newEstimatedTimeString.toInt()
                        }
                        val colorString = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                projectFragmentViewModel.colors.random()
                            )
                        ).substring(2).uppercase()

                        newProject.color = colorString

                        Log.d("ProjectFragment", newProject.color)

                        projectFragmentViewModel.insertProject(newProject) {

                            val database = getProjectDatabase(context)

                            // Launch a coroutine
                            lifecycleScope.launch {
                                // Perform the database operation within the coroutine
                                val projectList: List<Project> =
                                    withContext(Dispatchers.IO) {
                                        database.projectDatabaseDao.getAllNLD()
                                    }

                                val project2Add = projectList.last()

                                Log.d("ProjectFragment", currentUserEmail!!)

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
                                firebaseManager.addProject(project2Add, attributes)

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

        val projectDatabase = getProjectDatabase(requireContext())
        projectSnapshotListener.startListening(projectDatabase)
    }

    private fun animateFAB(isEmpty: Boolean) {
        val fab = projectFragmentBinding.floatingActionButton

        // Scale animation
        if (isEmpty) {
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

            val animationSet = AnimationSet(true).apply {
                addAnimation(scaleAnimation)
                addAnimation(rotate)
            }
            fab.startAnimation(animationSet)
        } else {
            fab.clearAnimation()
        }
    }
}