package com.example.deltatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    // Called when the activity is being created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.activity_main)

        // Get the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Handle the navigation up button press
    override fun onSupportNavigateUp(): Boolean {

        // Navigate up in the navigation hierarchy if possible
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
