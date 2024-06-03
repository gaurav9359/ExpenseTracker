package com.example.expensetracker.ui.screens

import TransactionAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityHomePageBinding
import com.example.expensetracker.models.TransactionModel
import com.example.expensetracker.ui.fragments.FragmentEditTransactionDetails
import com.example.expensetracker.viewmodels.HomeViewModel
import java.util.UUID

class HomePage : AppCompatActivity(), TransactionAdapter.OnItemClickListener {
    lateinit var binding: ActivityHomePageBinding
    private lateinit var adapter: TransactionAdapter
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TransactionAdapter(emptyList(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.totalBalanceValue.text = "$0.00"
        binding.totalCreditValue.text = "$0.00"
        binding.totalDebitValue.text = "$0.00"


        viewModel.isUserAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.addExpense.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.addExpense.visibility = View.GONE
                startActivity(Intent(this, SignInPage::class.java))
            }
        }

        binding.addExpense.setOnClickListener {
            showDialogue()
        }

        viewModel.transactions.observe(this) { transactions ->

            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.addExpense.visibility = View.GONE

            binding.recyclerView.postDelayed({
                adapter.updateTransactions(transactions)
                updateTotals()
                binding.recyclerView.post {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.addExpense.visibility = View.VISIBLE
                }
            }, 1000)
        }


        binding.logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialogue_confirmation, null)

            builder.setView(dialogView)
            val dialog = builder.create()

            dialogView.findViewById<TextView>(R.id.editTextTitle).text = "Confirm Logout"
            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

            confirmButton.setOnClickListener {
                viewModel.signOut()
                val intent = Intent(this, SignInPage::class.java)
                startActivity(intent)
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

        private fun showDialogue() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_add_expense, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val amountEditText = dialogView.findViewById<EditText>(R.id.editTextAmount)
        val typeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupType)
        val addButton = dialogView.findViewById<Button>(R.id.buttonAddExpense)
        val cancelButton = dialogView.findViewById<Button>(R.id.buttonCancel)

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val amountText = amountEditText.text.toString()
            val selectedTypeId = typeRadioGroup.checkedRadioButtonId
            val type = if (selectedTypeId == R.id.radioButtonDebit) "debit" else "credit"
            val amount = amountText.toFloatOrNull()

            if (title.isEmpty() || description.isEmpty() || amountText.isEmpty() || selectedTypeId == -1) {
                    Toast.makeText(this,"Fill all the Fields",Toast.LENGTH_SHORT).show()
            } else {
                if (amount != null) {
                    val transactionId = UUID.randomUUID().toString()
                    viewModel.addTransaction(
                        TransactionModel(
                            transactionId = transactionId,
                            title = title,
                            type = type,
                            description = description,
                            amount = amount
                        )
                    )
                    alertDialog.dismiss()
                    viewModel.loadTransactions()
                } else {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun updateTotals() {
        viewModel.calculateTotals()
        val totalCredit = viewModel.total_credit
        val totalDebit = viewModel.total_debit
        val totalBalance = viewModel.total_balance

        binding.totalCreditValue.text = "$$totalCredit"
        binding.totalDebitValue.text = "$$totalDebit"
        binding.totalBalanceValue.text = "$$totalBalance"
    }

    private fun showTransactionDetails(position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_transaction_details, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val titleTextView = dialogView.findViewById<TextView>(R.id.textViewTitle)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.textViewDescription)
        val amountTextView = dialogView.findViewById<TextView>(R.id.textViewAmount)
        val typeTextView = dialogView.findViewById<TextView>(R.id.textViewType)
        val updateButton = dialogView.findViewById<Button>(R.id.buttonUpdate)
        val deleteButton = dialogView.findViewById<Button>(R.id.buttonDelete)
        val goBackButton = dialogView.findViewById<Button>(R.id.buttonGoBack)

        val transaction = viewModel.transactions.value?.get(position)
        titleTextView.text = transaction?.title
        descriptionTextView.text = transaction?.description
        amountTextView.text = "$${transaction?.amount}"
        typeTextView.text = transaction?.type

        updateButton.setOnClickListener {
            val fragment = FragmentEditTransactionDetails.newInstance(transaction!!)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, fragment)
                addToBackStack(null)
            }
            binding.main.visibility=View.INVISIBLE
            alertDialog.dismiss()
        }

        deleteButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialogue_confirmation, null)

            builder.setView(dialogView)
            val dialog = builder.create()

            dialogView.findViewById<TextView>(R.id.editTextTitle).text = "Confirm Delete"
            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

            confirmButton.setOnClickListener {
                viewModel.deleteTransaction(transaction!!)
                viewModel.loadTransactions()
                dialog.dismiss()
                alertDialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
                alertDialog.dismiss()
            }

            dialog.show()


        }

        goBackButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onItemClick(position: Int) {
        showTransactionDetails(position)
    }
}
