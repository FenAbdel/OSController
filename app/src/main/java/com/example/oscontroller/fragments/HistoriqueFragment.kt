package com.example.oscontroller.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.oscontroller.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class HistoriqueFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the history screen
        return inflater.inflate(R.layout.hystorique_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up filter buttons
        val btnFilterDate: Button = view.findViewById(R.id.btn_filter_date)
        val btnFilterCategory: Button = view.findViewById(R.id.btn_filter_category)

        btnFilterDate.setOnClickListener {
            showDateFilterDialog()
        }

        btnFilterCategory.setOnClickListener {
            showCategoryFilterDialog()
        }

        // Initially load all transactions
        chargerHistoriqueTransactions()
    }

    private fun chargerHistoriqueTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("HistoriqueFragment", "User not logged in")
            return
        }
        val db = FirebaseFirestore.getInstance()
        val transactionsLayout = view?.findViewById<LinearLayout>(R.id.transaction_items_depense)
        val emptyMessage = view?.findViewById<TextView>(R.id.empty_message)

        db.collection("users").document(userId).collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                transactionsLayout?.removeAllViews()
                if (result.isEmpty) {
                    emptyMessage?.text = "Aucune transaction trouvée."
                    emptyMessage?.visibility = View.VISIBLE
                } else {
                    emptyMessage?.visibility = View.GONE
                    for (document in result) {
                        val transaction = document.data
                        val isA = transaction["is_a"] as? String ?: ""
                        if (isA.equals("Depence", ignoreCase = true)) {
                            afficherTransactionDepense(transaction)
                        } else if (isA.equals("Revenu", ignoreCase = true)) {
                            afficherTransactionRevenu(transaction)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HistoriqueFragment", "Error fetching transactions: ${exception.message}")
            }
    }

    private fun afficherTransactionDepense(transaction: Map<String, Any>) {
        val titre = transaction["title"] as? String ?: ""
        val montant = transaction["amount"] as? Double ?: 0.0
        val date = transaction["date"] as? String ?: ""
        val type = transaction["type"] as? String ?: ""

        val transactionsLayout = view?.findViewById<LinearLayout>(R.id.transaction_items_depense)
        val transactionView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_transactions_depence, transactionsLayout, false)

        val titreTextView = transactionView.findViewById<TextView>(R.id.transaction_titre)
        val montantTextView = transactionView.findViewById<TextView>(R.id.transaction_montant)
        val dateTextView = transactionView.findViewById<TextView>(R.id.transaction_date)
        val typeTextView = transactionView.findViewById<TextView>(R.id.transaction_type)

        titreTextView.text = titre
        dateTextView.text = date
        typeTextView.text = type
        montantTextView.text = "- $montant £"
        montantTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

        transactionsLayout?.addView(transactionView)
    }

    private fun afficherTransactionRevenu(transaction: Map<String, Any>) {
        val titre = transaction["title"] as? String ?: ""
        val montant = transaction["amount"] as? Double ?: 0.0
        val date = transaction["date"] as? String ?: ""
        val type = transaction["type"] as? String ?: ""

        val transactionsLayout = view?.findViewById<LinearLayout>(R.id.transaction_items_depense)
        val transactionView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_transactions_revenu, transactionsLayout, false)

        val titreTextView = transactionView.findViewById<TextView>(R.id.transaction_titre)
        val montantTextView = transactionView.findViewById<TextView>(R.id.transaction_montant)
        val dateTextView = transactionView.findViewById<TextView>(R.id.transaction_date)
        val typeTextView = transactionView.findViewById<TextView>(R.id.transaction_type)

        titreTextView.text = titre
        dateTextView.text = date
        typeTextView.text = type
        montantTextView.text = "+ $montant £"
        montantTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        transactionsLayout?.addView(transactionView)
    }

    // ********** Filter by Date Functionality **********

    private fun showDateFilterDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Format the selected date (e.g., "2025-02-25")
            val selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            filterTransactionsByDate(selectedDate)
        }, year, month, day).show()
    }

    private fun filterTransactionsByDate(date: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val transactionsLayout = view?.findViewById<LinearLayout>(R.id.transaction_items_depense)
        val emptyMessage = view?.findViewById<TextView>(R.id.empty_message)

        db.collection("users").document(userId).collection("transactions")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { result ->
                transactionsLayout?.removeAllViews()
                if (result.isEmpty) {
                    emptyMessage?.text = "Aucune transaction trouvée pour la date: $date"
                    emptyMessage?.visibility = View.VISIBLE
                } else {
                    emptyMessage?.visibility = View.GONE
                    for (document in result) {
                        val transaction = document.data
                        val isA = transaction["is_a"] as? String ?: ""
                        if (isA.equals("Depence", ignoreCase = true)) {
                            afficherTransactionDepense(transaction)
                        } else if (isA.equals("Revenu", ignoreCase = true)) {
                            afficherTransactionRevenu(transaction)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HistoriqueFragment", "Error filtering by date: ${exception.message}")
            }
    }

    // ********** Filter by Category Functionality **********

    private fun showCategoryFilterDialog() {
        // Define sample categories; adjust as needed or load from resources.
        val categoriesWithAll = arrayOf("All") + requireContext().resources.getStringArray(R.array.categories)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sélectionnez une catégorie")
        builder.setItems(categoriesWithAll) { _, which ->
            val selectedCategory = categoriesWithAll[which]
            if (selectedCategory == "All") {
                chargerHistoriqueTransactions()
            } else {
                filterTransactionsByCategory(selectedCategory)
            }
        }
        builder.create().show()
    }

    private fun filterTransactionsByCategory(category: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val transactionsLayout = view?.findViewById<LinearLayout>(R.id.transaction_items_depense)
        val emptyMessage = view?.findViewById<TextView>(R.id.empty_message)

        db.collection("users").document(userId).collection("transactions")
            .whereEqualTo("type", category)
            .get()
            .addOnSuccessListener { result ->
                transactionsLayout?.removeAllViews()
                if (result.isEmpty) {
                    emptyMessage?.text = "Aucune transaction trouvée pour la catégorie: $category"
                    emptyMessage?.visibility = View.VISIBLE
                } else {
                    emptyMessage?.visibility = View.GONE
                    for (document in result) {
                        val transaction = document.data
                        val isA = transaction["is_a"] as? String ?: ""
                        if (isA.equals("Depence", ignoreCase = true)) {
                            afficherTransactionDepense(transaction)
                        } else if (isA.equals("Revenu", ignoreCase = true)) {
                            afficherTransactionRevenu(transaction)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HistoriqueFragment", "Error filtering by category: ${exception.message}")
            }
    }
}
