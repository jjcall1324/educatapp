package com.example.educatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.educatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException




class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


        binding.btnSignUp.setOnClickListener {
            val email: String = binding.etEmail.text.toString().trim()
            val password: String = binding.etPassword.text.toString()

            // Validate the email and password (optional)
            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password is required"
                return@setOnClickListener
            }

            // Use the Firebase Authentication API to create a new user account
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign-up successful
                        val user = FirebaseAuth.getInstance().currentUser
                        Toast.makeText(this, "Sign-up successful: ${user?.email}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                        // TODO: Handle the successful sign-up flow (e.g., navigate to the next activity)
                    } else {
                        // Sign-up failed
                        val exception = task.exception
                        if (exception is FirebaseAuthException) {
                            val errorCode = exception.errorCode
                            val errorMessage = exception.message
                            Toast.makeText(this, "Sign-up failed: $errorCode - $errorMessage", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Sign-up failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }







    }
}