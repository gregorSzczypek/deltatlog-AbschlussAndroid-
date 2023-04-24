package com.example.deltatlog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentLandingPageBinding
import com.example.deltatlog.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        // Initializing firebase
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignMeUp.setOnClickListener {
            val email = binding.inputEmailAdress.text.toString()
            val pw = binding.inputPw1.text.toString()
            val pwConfirm = binding.inputPw2.text.toString()

            // Check of valid input and calling register method from firebase object
            if (email.isNotEmpty() && pw.isNotEmpty() && pwConfirm.isNotEmpty()) {

                if (pw == pwConfirm) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener {

                        if (it.isSuccessful) {
                            Toast.makeText(context, "Succesfully registered", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())

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

        // Navigation to sign up screen
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLandingPageFragment())
        }

    }
}