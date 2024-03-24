package com.se46.guardia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.se46.guardia.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth

class CheckSession : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance() // Initialize the FirebaseAuth instance
        checkUserSession() // Check if the user is already logged in

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.sUPbutton.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }

        binding.lInButton.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserSession() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startMainActivity()
        }
        // If currentUser is null, stay on the login page for the user to log in
    }

    private fun startMainActivity() {
        val intentOfMain = Intent(this, HomePage::class.java)
        startActivity(intentOfMain)
        finish() // Finish LoginActivity so user can't go back to it with back button
    }
}