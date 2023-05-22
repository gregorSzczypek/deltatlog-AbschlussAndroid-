package com.example.deltatlog.ui

import ProjectAdapter
import android.app.AlertDialog
import android.content.Context
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
import com.example.deltatlog.R
import com.example.deltatlog.TAG
import com.example.deltatlog.data.Repository
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.getDatabase
import com.example.deltatlog.data.local.getTaskDatabase
import com.example.deltatlog.data.remote.LogoApi
import com.example.deltatlog.databinding.FragmentProjectBinding
import com.example.deltatlog.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


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
                recyclerView.adapter = ProjectAdapter(viewModel, requireContext(), it)
            }
        )


        binding.floatingActionButton.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_dialogue_project, null)
            val newProjectName = dialogLayout.findViewById<EditText>(R.id.input_project_name)
            val newCustomerName =
                dialogLayout.findViewById<EditText>(R.id.input_project_customer_name)
            val newDescription = dialogLayout.findViewById<EditText>(R.id.input_project_description)
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

                            viewModel.insertProject(newProject)
                            val project2Add = viewModel.projectList.value!!.last()

                            // TODO add Project to firebase
                            Log.d("ProjectFragment", currentUserEmail!!)
                            Log.d("ProjectFragment", currentUserId!!.toString())

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

                            db.collection("users").document(currentUserId).collection("projects").document(project2Add.id.toString())
                                .set(firebaseItem2Add)
                                .addOnSuccessListener { Log.d("firebase", "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w("firebase", "Error writing document", e) }

                            Toast.makeText(
                                context,
                                "$newProjectNameString created",
                                Toast.LENGTH_SHORT
                            )
                                .show()
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

                        viewModel.insertProject(newProject)
                        val project2Add = viewModel.projectList.value!!.last()

                        Log.d("ProjectFragment", currentUserEmail!!)
                        Log.d("ProjectFragment", currentUserId!!.toString())

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
                        db.collection("users").document(currentUserId).collection("projects").document(project2Add.id.toString())
                            .set(firebaseItem2Add)
                            .addOnSuccessListener { Log.d("firebase", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("firebase", "Error writing document", e) }


                        Toast.makeText(
                            context,
                            "$newProjectNameString created",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
    }
}