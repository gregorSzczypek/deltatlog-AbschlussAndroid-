package com.example.deltatlog.util

import androidx.lifecycle.lifecycleScope
import androidx.room.withTransaction
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.local.ProjectDatabase
import com.example.deltatlog.ui.ProjectFragment
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProjectSnapshotListener(
    private val projectFragment: ProjectFragment,
    private val currentUserId: String
) {
    private val db = Firebase.firestore
    private var snapshotListener: ListenerRegistration? = null

    fun startListening(projectDatabase: ProjectDatabase) {
        val projectCollection = db.collection("users").document(currentUserId)
            .collection("projects")

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
                    image,
                    date,
                    description,
                    color,
                    numberOfTasks.toLong(),
                    totalTime
                )
                projects.add(project)
            }

            // Update the local Room database
            projectFragment.lifecycleScope.launch {

                // Run the database operation within a transaction
                projectDatabase.withTransaction {
                    // Retrieve the current projects from the database
                    val currentProjects = projectDatabase.projectDatabaseDao.getAllNLD()

                    // Compare the projects from Firestore with the current projects
                    val projectsToDelete = currentProjects.filter { it !in projects }
                    val projectsToInsert = projects.filter { it !in currentProjects }

                    // Delete projects that are no longer present in Firestore
                    projectDatabase.projectDatabaseDao.deleteProjects(projectsToDelete)

                    // Insert new projects from Firestore
                    projectDatabase.projectDatabaseDao.insertAll(projectsToInsert)
                }
            }
        }
    }

    fun stopListening() {
        snapshotListener!!.remove()
    }
}
