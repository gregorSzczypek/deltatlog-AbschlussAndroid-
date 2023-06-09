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
        // Check if the currentUserId is not null
        currentUserId?.let {
            // Access the Firestore database collection "users"
            db.collection("users")
                .document(currentUserId)
                .collection("tasks")
                .document(taskId)

                // Update the document with the provided updates
                .update(updates)
                .addOnSuccessListener {
                    // Log a success message when the document is successfully updated
                    Log.d("firebaseManager", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    // Log an error message if there is a failure updating the document
                    Log.w("firebaseManager", "Error updating document", e)
                }
        }
    }

    fun updateProjectChanges(projectId: String, updates: Map<String, Any>) {
        // Check if the currentUserId is not null
        currentUserId?.let {
            // Access the Firestore database collection "users"
            db.collection("users")
                .document(currentUserId)
                .collection("projects")
                .document(projectId)

                // Update the document with the provided updates
                .update(updates)
                .addOnSuccessListener {
                    // Log a success message when the document is successfully updated
                    Log.d("firebaseManager", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    // Log an error message if there is a failure updating the document
                    Log.w("firebaseManager", "Error updating document", e)
                }
        }
    }

    fun deleteTask(task: Task) {
        // Check if the currentUserId is not null
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("tasks")
                .document(task.id.toString())
                // delete the provided task
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
        // Check if the currentUserId is not null
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("projects")
                .document(project.id.toString())
                // delete the provided project
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
        // Check if the currentUserId is not null
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("tasks")
                // create a document for the new task
                .document(task.id.toString())
                // set the provided attributes
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
        // Check if the currentUserId is not null
        currentUserId?.let {
            db.collection("users").document(currentUserId)
                .collection("projects")
                // create a document for the new project
                .document(project.id.toString())
                // set the provided attributes
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

    fun deleteUserAssociatedData() {
        // Check if the currentUserId is not null
        currentUserId?.let {
            // Delete user tasks
            db.collection("users").document(currentUserId)
                .collection("tasks")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = db.batch()
                    for (document in querySnapshot) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            // Tasks deleted successfully
                            Log.d("firebase", "User tasks successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            // Error deleting tasks
                            Log.w("firebase", "Error deleting user tasks", e)
                        }
                }
                .addOnFailureListener { e ->
                    // Error retrieving tasks
                    Log.w("firebase", "Error retrieving user tasks", e)
                }

            // Delete user projects
            db.collection("users").document(currentUserId)
                .collection("projects")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = db.batch()
                    for (document in querySnapshot) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            // Projects deleted successfully
                            Log.d("firebase", "User projects successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            // Error deleting projects
                            Log.w("firebase", "Error deleting user projects", e)
                        }
                }
                .addOnFailureListener { e ->
                    // Error retrieving projects
                    Log.w("firebase", "Error retrieving user projects", e)
                }

            // Delete user document
            db.collection("users").document(currentUserId)
                .delete()
                .addOnSuccessListener {
                    // User document deleted successfully
                    Log.d("firebase", "User document successfully deleted!")
                }
                .addOnFailureListener { e ->
                    // Error deleting user document
                    Log.w("firebase", "Error deleting user document", e)
                }
        }
    }


    fun logOut(
        firebaseAuth: FirebaseAuth,
        currentUserEmail: String,
        context: Context
    ) {
        // sign out the current user from firebaseAuth
        firebaseAuth.signOut()

        // Check if the current user is null after signing out
        if (firebaseAuth.currentUser == null) {
//            projectFragmentViewModel.databaseDeleted = false

            // Display confirmation method if user is signed out
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

    fun deleteAccount(firebaseAuth: FirebaseAuth, currentUserEmail: String, context: Context) {
        val user = firebaseAuth.currentUser

        // Check if the user is signed in
        if (user != null) {
            // delete user associated data
            deleteUserAssociatedData()
            // Delete the user's account
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User account deleted successfully
                        Toast.makeText(
                            context,
                            "Successfully deleted account for user $currentUserEmail",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // An error occurred while deleting the account
                        Toast.makeText(
                            context,
                            "Failed to delete account: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            // User is already signed out
            Toast.makeText(
                context,
                "User $currentUserEmail is already signed out",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun changePassword(firebaseAuth: FirebaseAuth, newPassword: String, context: Context) {
        val user = firebaseAuth.currentUser

        // Check if the user is signed in
        if (user != null) {
            // Change the user's password
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password updated successfully
                        Toast.makeText(
                            context,
                            "Successfully changed password for user ${user.email}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // An error occurred while changing the password
                        Toast.makeText(
                            context,
                            "Failed to change password: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            // User is not signed in
            Toast.makeText(
                context,
                "No user is currently signed in",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun resetPassword(email: String, context: Context) {
        val firebaseAuth = FirebaseAuth.getInstance()

        // Check if the email is not empty
        if (email.isNotEmpty()) {
            // Send a password reset email to the provided email address
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        Toast.makeText(
                            context,
                            "Password reset email sent to $email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // An error occurred while sending the password reset email
                        Toast.makeText(
                            context,
                            "Failed to send password reset email: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            // Empty email is not allowed
            Toast.makeText(
                context,
                "Email field cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}