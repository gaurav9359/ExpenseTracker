package com.example.expensetracker.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.R
import com.example.expensetracker.models.TransactionModel
import com.example.expensetracker.ui.screens.HomePage
import com.example.expensetracker.ui.screens.SignInPage
import com.example.expensetracker.viewmodels.HomeViewModel

class FragmentEditTransactionDetails : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var transaction: TransactionModel? = null

    companion object {
        private const val ARG_TRANSACTION = "transaction"

        fun newInstance(transaction: TransactionModel): FragmentEditTransactionDetails {
            val fragment = FragmentEditTransactionDetails()
            val args = Bundle().apply {
                putParcelable(ARG_TRANSACTION, transaction)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        transaction = arguments?.getParcelable(ARG_TRANSACTION)

        val titleEditText = view.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = view.findViewById<EditText>(R.id.editTextDescription)
        val amountEditText = view.findViewById<EditText>(R.id.editTextAmount)
        val typeRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupType)
        val creditRadioButton = view.findViewById<RadioButton>(R.id.radioButtonCredit)
        val debitRadioButton = view.findViewById<RadioButton>(R.id.radioButtonDebit)
        val updateButton = view.findViewById<Button>(R.id.buttonUpdate)
        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)
        val goBackButton = view.findViewById<Button>(R.id.buttonGoBack)

        transaction?.let {
            titleEditText.setText(it.title)
            descriptionEditText.setText(it.description)
            amountEditText.setText(it.amount.toString())
            if (it.type == "credit") {
                creditRadioButton.isChecked = true
            } else {
                debitRadioButton.isChecked = true
            }
        }

        updateButton.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialogue_confirmation, null)

            builder.setView(dialogView)
            val dialog = builder.create()

            dialogView.findViewById<TextView>(R.id.editTextTitle).text = "Confirm Update"
            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

            confirmButton.setOnClickListener {
                val updatedTitle = titleEditText.text.toString()
                val updatedDescription = descriptionEditText.text.toString()
                val updatedAmount = amountEditText.text.toString().toFloatOrNull()
                val updatedType = if (creditRadioButton.isChecked) "credit" else "debit"

                if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedAmount!!.equals("") || (!creditRadioButton.isChecked && !debitRadioButton.isChecked)) {
                    Toast.makeText(requireContext(),"Enter all the fields",Toast.LENGTH_SHORT).show()
                } else {
                    if (updatedAmount != null) {
                        transaction?.let {
                            val updatedTransaction = it.copy(
                                title = updatedTitle,
                                description = updatedDescription,
                                amount = updatedAmount,
                                type = updatedType
                            )
                            viewModel.updateTransaction(updatedTransaction)
                            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                            requireActivity().supportFragmentManager.popBackStack()
                            (requireActivity() as HomePage).binding.main.visibility = View.VISIBLE
                        }
                    }
                    dialog.dismiss()
                }

            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()


        }

        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialogue_confirmation, null)

            builder.setView(dialogView)
            val dialog = builder.create()

            dialogView.findViewById<TextView>(R.id.editTextTitle).text = "Confirm Delete"
            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

            confirmButton.setOnClickListener {
                transaction?.let {

                    viewModel.deleteTransaction(it)
                    val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    requireActivity().supportFragmentManager.popBackStack()
                    (requireActivity() as HomePage).binding.main.visibility = View.VISIBLE
                }
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }



        goBackButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialogue_confirmation, null)

            builder.setView(dialogView)
            val dialog = builder.create()

            dialogView.findViewById<TextView>(R.id.editTextTitle).text = "Changes Made won't be Saved,Confirm Go Back"
            val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

            confirmButton.setOnClickListener {
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                requireActivity().supportFragmentManager.popBackStack()
                (requireActivity() as HomePage).binding.main.visibility = View.VISIBLE
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()


        }
    }
}
