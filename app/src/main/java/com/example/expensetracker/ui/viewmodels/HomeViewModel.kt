package com.example.expensetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.expensetracker.auth.FirebaseAuthRepository
import com.example.expensetracker.data.FirebaseRepository
import com.example.expensetracker.models.TransactionModel

class HomeViewModel : ViewModel() {
    private val firebaseRepository = FirebaseRepository()
    private val firebaseAuthRepository = FirebaseAuthRepository()
    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> = _transactions
    private val _isUserAuthenticated = MutableLiveData<Boolean>()
    val isUserAuthenticated: LiveData<Boolean> = _isUserAuthenticated
    var total_balance:Float=0f
    var total_credit:Float=0f
    var total_debit:Float=0f

    init {
        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        val currentUser = firebaseAuthRepository.getCurrentUser()
        _isUserAuthenticated.value = currentUser != null
        if (currentUser != null) {
            loadTransactions()
        }
    }

    public fun loadTransactions() {
        firebaseRepository.getTransactions { transactions ->
            _transactions.value = transactions.reversed()
        }
    }

    fun addTransaction(transaction: TransactionModel) {
        firebaseRepository.addTransaction(transaction)
        loadTransactions()
    }

    fun deleteTransaction(transaction: TransactionModel) {
        firebaseRepository.deleteTransaction(transaction)
        loadTransactions()
    }

    fun updateTransaction(transaction: TransactionModel){
        firebaseRepository.updateTransaction(transaction)
        loadTransactions()

    }

    fun signUp(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuthRepository.signUp(email, password) { isSuccessful ->
            if (isSuccessful) {
                checkUserAuthentication()
            }
            onComplete(isSuccessful)
        }
    }

    fun signIn(email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuthRepository.signIn(email, password) { isSuccessful ->
            if (isSuccessful) {
                checkUserAuthentication()
            }
            onComplete(isSuccessful)
        }
    }

    fun signOut() {
        firebaseAuthRepository.signOut()
        _isUserAuthenticated.value = false
        _transactions.value = emptyList()
    }

    public fun calculateTotals() {
        var credit = 0f
        var debit = 0f
        val transactionsForLoop = _transactions.value ?: return
        for (transaction in transactionsForLoop) {
            if (transaction.type == "credit") {
                credit += transaction.amount
            } else if (transaction.type == "debit") {
                debit += transaction.amount
            }
        }

        total_credit = credit
        total_debit = debit
        total_balance = total_credit - total_debit
    }

}
