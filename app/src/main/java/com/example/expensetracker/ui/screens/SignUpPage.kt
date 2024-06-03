package com.example.expensetracker.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expensetracker.R
import com.example.expensetracker.auth.FirebaseAuthRepository
import com.example.expensetracker.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpPage : AppCompatActivity() {
    lateinit var binding: ActivitySignUpPageBinding
    private lateinit var firebaseAuth: FirebaseAuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuthRepository()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInPage::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()
            val confirmPass = binding.confirmPass.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.signUp(email,pass) { isSuccessful ->
                        if (isSuccessful) {
                            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomePage::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

    }
}