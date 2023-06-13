package com.example.deltatlog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.deltatlog.R
import com.example.deltatlog.databinding.FragmentManualBinding


class ManualFragment : Fragment() {

    // Variable to hold the binding for the fragment
    private lateinit var manualBinding: FragmentManualBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment's layout using data binding
        manualBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manual,
            container,
            false
        )
        // Return the root view of the fragment
        return manualBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set onClickListener to BackButton Navigation in Toolbar
        manualBinding.materialToolbar.setNavigationOnClickListener {
            // Navigate to Home Fragment
            findNavController().navigate(ManualFragmentDirections.actionManualFragmentToProjectFragment())
        }
    }
}