package com.example.expensetracker.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun signIn(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}