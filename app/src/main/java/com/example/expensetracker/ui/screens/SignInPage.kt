package com.example.expensetracker.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expensetracker.R
import com.example.expensetracker.auth.FirebaseAuthRepository
import com.example.expensetracker.databinding.ActivityHomePageBinding
import com.example.expensetracker.databinding.ActivitySignInPageBinding
import com.example.expensetracker.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignInPage : AppCompatActivity() {
    lateinit var binding: ActivitySignInPageBinding
    private lateinit var firebaseAuth: FirebaseAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivitySignInPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuthRepository()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signIn(email,pass){isSuccessful->
                    if (isSuccessful) {
                        Log.d("orewa","roger")
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                        Log.d("oreno","nawa")
                        Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "not signed in", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()

                    }
                }

            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
    }
}
}