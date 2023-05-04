package com.example.deltatlog.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.SharedViewModel
import com.example.deltatlog.databinding.FragmentLoginBinding
import com.example.deltatlog.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.concurrent.fixedRateTimer


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var userMail: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // // this click listener performs the login procedure in firebase after checks of valid input
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmailAdress.text.toString()
            val pw = binding.inputPw1.text.toString()
            sharedViewModel.login(requireContext(), email, pw, findNavController())
        }

        // BackButton Navigation in Toolbar
        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Navigation to sign up page in case user is not a member yet
        binding.tvNotRegistered.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
    }
}

//sharedViewModel.currentUser.observe(viewLifecycleOwner) {
//            if (it != null) {
//                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
//            }
//        }