package com.example.oscontroller.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.oscontroller.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_activity, container, false)
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bouton pour afficher le formulaire de dépense
        val buttonAjouterDepense: Button = view.findViewById(R.id.ajouter_depense)
        buttonAjouterDepense.setOnClickListener {
            afficherFormulaireDepense()
        }

        val buttonAjouterRevenu: Button = view.findViewById(R.id.ajouter_revenu)
        buttonAjouterRevenu.setOnClickListener {
            afficherFormulaireRevenu()
        }

    }
    //ading to firestor database
    data class Transaction(
        val title: String,
        val amount: Double,
        val date: String,
        val type: String
    )
    fun addTransactionToFirebase(title: String, amount: Double, type: String, date: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid -> // Ensure user is logged in
            var finalDate = date

            if (finalDate.isNullOrEmpty()) {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                finalDate = formatter.format(Date()) // Set current date if empty
            }

            val transaction = hashMapOf(
                "title" to title,
                "amount" to amount,
                "date" to finalDate,
                "type" to type
            )

            // Add transaction to the "transactions" subcollection inside the user's document
            db.collection("users").document(uid).collection("transactions")
                .add(transaction)
                .addOnSuccessListener {
                    Log.d("Firebase", "Transaction added successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding transaction: ${exception.message}")
                }
        } ?: Log.e("Firebase", "User not logged in") // Handle case when user is not authenticated
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun afficherFormulaireDepense() {
        // Charger le layout personnalisé
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_ajouter_depense, null)

        // Créer un dialog personnalisé
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()


        // Configuration du Spinner
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_type)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Récupérer les champs et les boutons
        val editType = dialogView.findViewById<EditText>(R.id.edit_type)
        val editDate = dialogView.findViewById<EditText>(R.id.edit_date)
        val editMontant = dialogView.findViewById<EditText>(R.id.edit_montant)
        val buttonAjouter = dialogView.findViewById<Button>(R.id.button_ajouter)
        val buttonAnnuler = dialogView.findViewById<Button>(R.id.button_annuler)

        // Action pour le bouton "Ajouter"
        buttonAjouter.setOnClickListener {
            val titre = editType.text.toString()
            val date = editDate.text.toString()
            val montant = editMontant.text.toString()
            val montant_i = editMontant.text.toString().toDoubleOrNull()
            val type = spinner.selectedItem.toString()
            var finalDate = date

            if (finalDate.isNullOrEmpty()) {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                finalDate = formatter.format(Date()) // Renvoie la date formatée
            }

            if (titre.isNotEmpty() && type.isNotEmpty()  && montant.isNotEmpty()) {
                // Récupérer le LinearLayout parent où les transactions sont affichées
                val transactionsLayout = requireView().findViewById<LinearLayout>(R.id.transaction_items_depense)

                if (transactionsLayout == null) {
                    Log.e("DEBUG_TAG", "transactions_container non trouvé. Vérifiez le fichier XML.")
                }



                // Charger le layout `transaction_items.xml`
                val transactionView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.layout_transactions_depence, transactionsLayout, false)

                // Remplir les données dans les TextView du layout
                val titreTextView = transactionView.findViewById<TextView>(R.id.transaction_titre)
                val montantTextView = transactionView.findViewById<TextView>(R.id.transaction_montant)
                val dateTextView = transactionView.findViewById<TextView>(R.id.transaction_date)
                val typeTextView = transactionView.findViewById<TextView>(R.id.transaction_type)

                titreTextView.text = titre
                montantTextView.text = "- $montant €"
                montantTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

                dateTextView.text = finalDate
                typeTextView.text = type

                // Ajouter le layout rempli au parent
                transactionsLayout.addView(transactionView)

                // Afficher un message de confirmation
                Toast.makeText(requireContext(), "Dépense ajoutée : $titre, $type, $date, $montant£", Toast.LENGTH_SHORT).show()

                // Fermer le dialog
                dialog.dismiss()
                if (montant_i != null) {
                    addTransactionToFirebase( titre, montant_i, type, finalDate)
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }
        buttonAnnuler.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun afficherFormulaireRevenu() {
        // Charger le layout personnalisé
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_ajouter_revenu, null)

        // Créer un dialog personnalisé
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()


        // Configuration du Spinner
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_type_revenu)
        ArrayAdapter.createFromResource(
            requireContext(),

            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Récupérer les champs et les boutons
        val editType = dialogView.findViewById<EditText>(R.id.edit_type)
        val editDate = dialogView.findViewById<EditText>(R.id.edit_date)
        val editMontant = dialogView.findViewById<EditText>(R.id.edit_montant)
        val buttonAjouter = dialogView.findViewById<Button>(R.id.button_ajouter)
        val buttonAnnuler = dialogView.findViewById<Button>(R.id.button_annuler)

        // Action pour le bouton "Ajouter"
        buttonAjouter.setOnClickListener {
            val titre = editType.text.toString()
            val date = editDate.text.toString()
            val montant = editMontant.text.toString()
            val montant_i = editMontant.text.toString().toDoubleOrNull()
            val type = spinner.selectedItem.toString()
            var finalDate = date

            if (finalDate.isNullOrEmpty()) {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                finalDate = formatter.format(Date()) // Renvoie la date formatée
            }

            if (titre.isNotEmpty() && type.isNotEmpty()  && montant.isNotEmpty()) {
                // Récupérer le LinearLayout parent où les transactions sont affichées
                val transactionsLayout = requireView().findViewById<LinearLayout>(R.id.transaction_items_depense)

                if (transactionsLayout == null) {
                    Log.e("DEBUG_TAG", "transactions_container non trouvé. Vérifiez le fichier XML.")
                }



                // Charger le layout `transaction_items.xml`
                val transactionView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.layout_transactions_depence, transactionsLayout, false)

                // Remplir les données dans les TextView du layout
                val titreTextView = transactionView.findViewById<TextView>(R.id.transaction_titre)
                val montantTextView = transactionView.findViewById<TextView>(R.id.transaction_montant)
                val dateTextView = transactionView.findViewById<TextView>(R.id.transaction_date)
                val typeTextView = transactionView.findViewById<TextView>(R.id.transaction_type)

                titreTextView.text = titre
                montantTextView.text = "+ $montant €"
                montantTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

                dateTextView.text = finalDate
                typeTextView.text = type

                // Ajouter le layout rempli au parent
                transactionsLayout.addView(transactionView)

                // Afficher un message de confirmation
                Toast.makeText(requireContext(), "Revenu ajoutée : $titre, $type, $date, $montant£", Toast.LENGTH_SHORT).show()

                // Fermer le dialog
                dialog.dismiss()
                if (montant_i != null) {
                    addTransactionToFirebase( titre, montant_i, type, finalDate)
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }
        buttonAnnuler.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



}