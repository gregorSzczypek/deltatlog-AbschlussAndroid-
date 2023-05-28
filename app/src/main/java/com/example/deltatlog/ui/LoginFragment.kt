package com.example.deltatlog.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.FirebaseManager
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    // Variable to hold the binding for the fragment
    private lateinit var LoginBinding: FragmentLoginBinding
    // ViewModel instance associated with the fragment
    private val firebaseManager = FirebaseManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout using data binding
        LoginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        // Return the root view of the fragment
        return LoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // click listener for the login button
        LoginBinding.btnLogin.setOnClickListener {
            // get the entered email adress
            val email = LoginBinding.inputEmailAdress.text.toString()
            // get the entered password
            val pw = LoginBinding.inputPw1.text.toString()
            // perform the login procedure
            firebaseManager.login(requireContext(), email, pw, findNavController())
        }

        // BackButton listener Navigation in Toolbar
        LoginBinding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Click listener for the "Not registered?" text view
        LoginBinding.tvNotRegistered.setOnClickListener {
            // Navigate to the sign-up fragment
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
    }
}