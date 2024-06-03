package com.example.expensetracker.data

import android.util.Log
import com.example.expensetracker.models.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun addTransaction(transaction: TransactionModel) {
        val userId = currentUser?.uid ?: return
        val transactionId = database.child("users").child(userId).child("transactions").push().key ?: return
        transaction.transactionId = transactionId
        database.child("users").child(userId).child("transactions").child(transactionId).setValue(transaction)
    }


    fun getTransactions(callback: (List<TransactionModel>) -> Unit) {
        val userId = currentUser?.uid ?: return
        database.child("users").child(userId).child("transactions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactions = mutableListOf<TransactionModel>()
                    for (transactionSnapshot in snapshot.children) {
                        val transaction = transactionSnapshot.getValue(TransactionModel::class.java)
                        transaction?.let { transactions.add(it) }
                    }
                    callback(transactions)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun deleteTransaction(transaction: TransactionModel) {
        val userId = currentUser?.uid ?: return
        val transactionId = transaction.transactionId
        database.child("users").child(userId).child("transactions").child(transactionId!!).removeValue()
    }

    fun updateTransaction(transaction: TransactionModel) {
        val userId = currentUser?.uid ?: return
        val transactionId = transaction.transactionId ?: return
        database.child("users").child(userId).child("transactions").child(transactionId).setValue(transaction)
    }

}
