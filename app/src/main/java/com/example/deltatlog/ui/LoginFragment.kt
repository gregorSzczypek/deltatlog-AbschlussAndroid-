package com.example.deltatlog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentLoginBinding
import com.example.deltatlog.databinding.FragmentSignUpBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

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

        // Navigation to sign up screen
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLandingPageFragment())
        }

        // Navigation to login screen
        binding.btnLogin.setOnClickListener {
            //TODO go initiate login procedure, check if it was successfull
            //TODO navigate to home
        }
    }
}