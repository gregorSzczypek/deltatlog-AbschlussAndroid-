package com.example.deltatlog.util

import androidx.lifecycle.lifecycleScope
import androidx.room.withTransaction
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.data.local.TaskDatabase
import com.example.deltatlog.ui.TaskFragment
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class TaskSnapshotListener (
    private val taskFragment: TaskFragment,
    private val currentUserId: String
    ) {
        private val db = Firebase.firestore
        private var snapshotListener: ListenerRegistration? = null

        fun startListening(taskDatabase: TaskDatabase) {
            val taskCollection = db.collection("users").document(currentUserId)
                .collection("tasks")

            // Start listening to changes in the Firestore projects collection
            snapshotListener = taskCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val tasks = mutableListOf<Task>()

                // Iterate through the documents in the Firestore snapshot
                for (doc in snapshot?.documents ?: emptyList()) {
                    val id = doc.id.toLong()
                    val taskProjectId = doc.getLong("taskProjectId")?: 0
                    val name = doc.getString("name") ?: ""
                    val color = doc.getString("color") ?: ""
                    val date = doc.getString("date") ?: ""
                    val duration = doc.getString("duration") ?: ""
                    val description = doc.getString("description") ?: ""
                    val notes = doc.getString("notes") ?: ""
                    val elapsedTime = doc.getLong("elapsedTime")?: 0

                    // Create a Task object
                    val task = Task(
                        id,
                        taskProjectId,
                        name,
                        color,
                        date,
                        duration,
                        description,
                        notes,
                        elapsedTime,
                    )
                    tasks.add(task)
                }

                // Update the local Room database
                taskFragment.lifecycleScope.launch {

                    // Run the database operation within a transaction
                    taskDatabase.withTransaction {
                        // Retrieve the current projects from the database
                        val currentTasks = taskDatabase.taskDatabaseDao.getAllNLD()

                        // Compare the projects from Firestore with the current projects
                        val tasksToDelete = currentTasks.filter { it !in tasks }
                        val tasksToInsert = tasks.filter { it !in currentTasks }

                        // Delete projects that are no longer present in Firestore
                        taskDatabase.taskDatabaseDao.deleteTasks(tasksToDelete)

                        // Insert new projects from Firestore
                        taskDatabase.taskDatabaseDao.insertAll(tasksToInsert)
                    }
                }
            }
        }

        fun stopListening() {
            // Stop listening to changes in the Firestore projects collection
            snapshotListener!!.remove()
        }
}