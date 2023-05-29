package com.example.deltatlog.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.deltatlog.data.datamodels.Project
import com.example.deltatlog.data.datamodels.Task
import com.example.deltatlog.ui.LoginFragmentDirections
import com.example.deltatlog.ui.SignUpFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseManager {

    private val db = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId = firebaseAuth.currentUser?.uid

    fun updateTaskChanges(taskId: String, updates: Map<String, Any>) {
        currentUserId?.let {
            db.collection("users")
                .document(currentUserId)
                .collection("tasks")
                .document(taskId)
                .update(updates)
                .addOnSuccessListener {
                    Log.d("firebaseManager", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("firebaseManager", "Error updating document", e)
                }
        }
    }

    fun updateProjectChanges(projectId: String, updates: Map<String, Any>) {
        currentUserId?.let {
            db.collection("users")
                .document(currentUserId)
                .collection("projects")
                .document(projectId)
                .update(updates)
                .addOnSuccessListener {
                    Log.d("firebaseManager", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("firebaseManager", "Error updating document", e)
                }
        }
    }

    fun deleteTask(task: Task) {
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("tasks")
                .document(task.id.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(
                        "firebase",
                        "DocumentSnapshot successfully deleted!"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(
                        "firebase",
                        "Error deleting document",
                        e
                    )
                }
        }
    }

    fun deleteProject(project: Project) {
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("projects")
                .document(project.id.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(
                        "firebase",
                        "DocumentSnapshot successfully deleted!"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(
                        "firebase",
                        "Error deleting document",
                        e
                    )
                }
        }
    }

    fun addTask(task: Task, attributes: HashMap<String, Any>) {
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("tasks")
                .document(task.id.toString())
                .set(attributes)
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
        }
    }

    fun addProject(project: Project, attributes: HashMap<String, Any>) {
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("projects")
                .document(project.id.toString())
                .set(attributes)
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
        }
    }

    fun logOut(
        firebaseAuth: FirebaseAuth,
        currentUserEmail: String,
        context: Context
    ) {
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser == null) {
//            projectFragmentViewModel.databaseDeleted = false
            Toast.makeText(
                context,
                "Successfully logged out user $currentUserEmail",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun signUp(
        context: Context,
        email: String,
        pw: String,
        pwConfirm: String,
        navController: NavController
    ) {

        // Check of valid input and calling register method from firebase object
        if (email.isNotEmpty() && pw.isNotEmpty() && pwConfirm.isNotEmpty()) {

            if (pw == pwConfirm) {
                firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Succesfully registered", Toast.LENGTH_SHORT).show()
                        // Navigation to login page after registration
                        navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                    } else {
                        Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }

    fun login(context: Context, email: String, pw: String, navController: NavController) {
//        viewModel.databaseDeleted = false
        // Check of valid input and calling login method from firebase object
        if (email.isNotEmpty() && pw.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Succesfully signed in user ${firebaseAuth.currentUser?.email}",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
        }
    }
}