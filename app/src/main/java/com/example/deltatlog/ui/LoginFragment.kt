package com.example.deltatlog.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.util.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    // Variable to hold the binding for the fragment
    private lateinit var loginBinding: FragmentLoginBinding
    // ViewModel instance associated with the fragment
    private val firebaseManager = FirebaseManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment's layout using data binding
        loginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        // Return the root view of the fragment
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // click listener for the login button
        loginBinding.btnLogin.setOnClickListener {
            // get the entered email adress
            val email = loginBinding.inputEmailAdress.text.toString()
            // get the entered password
            val pw = loginBinding.inputPw1.text.toString()
            // perform the login procedure
            firebaseManager.login(requireContext(), email, pw, findNavController())
        }

        // BackButton listener Navigation in Toolbar
        loginBinding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Click listener for the "Not registered?" text view
        loginBinding.tvNotRegistered.setOnClickListener {
            // Navigate to the sign-up fragment
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        // Click listener for the password forgotten text view
        loginBinding.tvPasswordForgotten!!.setOnClickListener {
            val dialogView = LinearLayout(context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialogView.layoutParams = layoutParams
            dialogView.orientation = LinearLayout.VERTICAL

            val emailEditText = EditText(context)
            emailEditText.hint = "Email"
            dialogView.addView(emailEditText)

            val confirmEmailEditText = EditText(context)
            confirmEmailEditText.hint = "Confirm Email"
            dialogView.addView(confirmEmailEditText)

            val dialog = AlertDialog.Builder(context)
                .setTitle("Reset Password")
                .setView(dialogView)
                .setPositiveButton("Reset") { _, _ ->
                    val email = emailEditText.text.toString().trim()
                    val confirmEmail = confirmEmailEditText.text.toString().trim()

                    if (email == confirmEmail) {
                        firebaseManager.resetPassword(email, requireContext())
                    } else {
                        Toast.makeText(context, "Emails do not match", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }
    }
}