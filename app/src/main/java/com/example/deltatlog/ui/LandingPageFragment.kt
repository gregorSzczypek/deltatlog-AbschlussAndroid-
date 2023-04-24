//package com.example.deltatlog.ui
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.example.deltatlog.R
//import com.example.deltatlog.databinding.FragmentLandingPageBinding
//
//
//class LandingPageFragment : Fragment() {
//
//    private lateinit var binding: FragmentLandingPageBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = DataBindingUtil.inflate(
//            inflater,
//            R.layout.fragment_landing_page,
//            container,
//            false
//        )
//
//        // Inflate the layout for this fragment
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Navigation to sign up screen
//        binding.btnSignUp.setOnClickListener {
//            findNavController().navigate(LandingPageFragmentDirections.actionLandingPageFragmentToSignUpFragment())
//        }
//
//        // Navigation to login screen
//        binding.btnLogin.setOnClickListener {
//            findNavController().navigate(LandingPageFragmentDirections.actionLandingPageFragmentToLoginFragment())
//        }
//    }
//}