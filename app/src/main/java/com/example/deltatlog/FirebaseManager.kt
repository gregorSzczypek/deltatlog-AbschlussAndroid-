package com.example.deltatlog

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.ui.ProjectFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseManager {

    private val db = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun updateTaskChanges(taskId: String, updates: Map<String, Any>) {
        val currentUserId = firebaseAuth.currentUser?.uid
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

    fun logOut(firebaseAuth: FirebaseAuth, projectFragmentViewModel: viewModel, currentUserEmail: String, context: Context) {
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser == null) {
            projectFragmentViewModel.databaseDeleted = false
            Toast.makeText(
                context,
                "Successfully logged out user $currentUserEmail",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}