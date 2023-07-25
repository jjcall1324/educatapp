package com.example.educatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import com.example.educatapp.databinding.ActivityMainBinding
import com.example.educatapp.databinding.ActivitySignUpBinding
import com.example.educatapp.ui.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp







class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.profileFragment)
        }



        drawerLayout = binding.drawerLayout
        navView = binding.navView

        FirebaseApp.initializeApp(this)



        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_content, R.id.discussion
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(R.id.nav_home)
            val headerView: View = navView.getHeaderView(0)
            val emailTextView: TextView = headerView.findViewById(R.id.textView)
            emailTextView.text = currentUser.email

        }






    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signoutBTN -> {
                // Handle sign-out button click
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        // Perform sign-out operation
        FirebaseAuth.getInstance().signOut()

        // Redirect the user to the login screen or any other desired destination
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish() // Optional: Close the current activity to prevent going back to the signed-in state
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}