package com.example.oscontroller.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.oscontroller.MainActivity
import com.example.oscontroller.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ParametreFragment : Fragment() {
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>
    private var pendingCSVContent: String? = null
    // Retrieve the budget value entered by the user:


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the document creation launcher for CSV files
        createDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("text/csv")
        ) { uri: Uri? ->
            if (uri != null) {
                writeCSVToUri(uri, pendingCSVContent)
            } else {
                Log.e("ParametreFragment", "Document creation was cancelled.")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_settings, container, false)

        // Find the Spinner in the layout
        val deviseSpinner: Spinner = view.findViewById(R.id.devise_spinner)

        // Retrieve the budget value entered by the user:
        val budgetEditText: EditText = view.findViewById(R.id.budgetmensuel)
        val budgetValue = budgetEditText.text.toString().toFloatOrNull() ?: 0f

        // Create an ArrayAdapter using the string-array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.device, // The string-array defined in strings.xml
            android.R.layout.simple_spinner_item // Default spinner layout
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the Spinner
        deviseSpinner.adapter = adapter

        // Find and set up sign-out button
        val signOutButton: Button = view.findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {
            showSignOutConfirmationDialog()
        }

        val extractButton: Button = view.findViewById(R.id.save_data_button)
        extractButton.setOnClickListener { exportUserDataToCSV() }

        val gestionButton: Button = view.findViewById(R.id.gestion_button)
        gestionButton.setOnClickListener {
            checkBudgetAndNotify()
        }

        return view

    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Se déconnecter")
            .setMessage("Êtes-vous sûr de vouloir vous déconnecter?")
            .setPositiveButton("Sign Out") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Annuler", null)
            .create()
            .show()
    }


    private fun exportUserDataToCSV() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("ParametreFragment", "User not logged in")
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                val sb = StringBuilder()
                // CSV Header – adjust columns as needed
                sb.append("Date,Title,Amount,Type,is_a\n")
                for (document in result) {
                    val data = document.data
                    val date = data["date"] as? String ?: ""
                    val title = data["title"] as? String ?: ""
                    val amount = data["amount"]?.toString() ?: ""
                    val type = data["type"] as? String ?: ""
                    val isA = data["is_a"] as? String ?: ""
                    // Build CSV row (ensure commas within fields are handled if needed)
                    sb.append("$date,$title,$amount,$type,$isA\n")
                }
                pendingCSVContent = sb.toString()
                // Launch the document creation intent with a suggested file name
                createDocumentLauncher.launch("userdata.csv")
            }
            .addOnFailureListener { exception ->
                Log.e("ParametreFragment", "Error fetching transactions: ${exception.message}")
            }
    }


    private fun writeCSVToUri(uri: Uri, csvContent: String?) {
        if (csvContent == null) return
        try {
            val outputStream = requireContext().contentResolver.openOutputStream(uri)
            outputStream?.bufferedWriter().use { writer ->
                writer?.write(csvContent)
            }
            Log.d("ParametreFragment", "CSV export successful")
        } catch (e: Exception) {
            Log.e("ParametreFragment", "Error writing CSV: ${e.message}")
        }
    }

    private fun monitorBudget() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Listen to all expense transactions
        db.collection("users").document(userId).collection("transactions")
            .whereEqualTo("is_a", "Depence")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ParametreFragment", "Listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // Sum up the expenses for the current month
                    val totalExpense = snapshot.documents.fold(0f) { acc, doc ->
                        val dateStr = doc.getString("date") ?: ""
                        // Assuming date is in "yyyy-MM-dd" format, check if it is in the current month:
                        if (isCurrentMonth(dateStr)) {
                            val amount = (doc.get("amount") as? Number)?.toFloat() ?: 0f
                            acc + amount
                        } else {
                            acc
                        }
                    }
                    // Compare with the budget value (you might retrieve this from SharedPreferences or your EditText)
                    val budgetValue = getMonthlyBudget()  // see Step 3 below
                    if (budgetValue > 0 && totalExpense > budgetValue) {
                        // Budget exceeded – trigger email notification
                        notifyUserByEmail(totalExpense, budgetValue)
                    }
                }
            }
    }
    private fun isCurrentMonth(dateStr: String): Boolean {
        // Assuming dateStr is in "yyyy-MM-dd"
        try {
            val parts = dateStr.split("-")
            if (parts.size < 2) return false
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1  // Calendar.MONTH is zero-indexed
            return (year == currentYear && month == currentMonth)
        } catch (e: Exception) {
            return false
        }
    }
    private fun getMonthlyBudget(): Float {
        val sharedPref = requireContext().getSharedPreferences("UserSettings", 0)
        return sharedPref.getFloat("monthly_budget", 0f)
    }

    private fun saveMonthlyBudget(budget: Float) {
        val sharedPref = requireContext().getSharedPreferences("UserSettings", 0)
        sharedPref.edit().putFloat("monthly_budget", budget).apply()
    }
    private fun checkBudgetAndNotify() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val budgetValue = getMonthlyBudget()
        if (budgetValue == 0f) return // No budget set

        db.collection("users").document(userId).collection("transactions")
            .whereEqualTo("is_a", "Depence")
            .get()
            .addOnSuccessListener { result ->
                val totalExpense = result.documents.fold(0f) { acc, doc ->
                    val dateStr = doc.getString("date") ?: ""
                    if (isCurrentMonth(dateStr)) {
                        val amount = (doc.get("amount") as? Number)?.toFloat() ?: 0f
                        acc + amount
                    } else {
                        acc
                    }
                }
                if (totalExpense > budgetValue) {
                    notifyUserByEmail(totalExpense, budgetValue)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ParametreFragment", "Error checking budget: ${exception.message}")
            }
    }
    private fun notifyUserByEmail(totalExpense: Float, budgetValue: Float) {
        // Get the current user's email (if available)
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        val subject = "Budget Alert: Expenses Exceeded"
        val body = "Your expenses for this month have reached £$totalExpense, which exceeds your set budget of £$budgetValue."

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")  // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        // This will prompt the user to send the email
        startActivity(emailIntent)
    }






}