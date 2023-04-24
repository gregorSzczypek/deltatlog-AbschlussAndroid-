package com.example.deltatlog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentLandingPageBinding
import com.example.deltatlog.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sign_up,
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
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLandingPageFragment())
        }

        // Navigation to login screen
        binding.btnSignMeUp.setOnClickListener {
            //TODO go initiate sign up procedure, check if it was successfull
            //TODO navigate to home
        }
    }
}