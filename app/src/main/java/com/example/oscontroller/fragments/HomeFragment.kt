package com.example.oscontroller.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import com.example.oscontroller.R

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
            val type = spinner.selectedItem.toString()

            if (titre.isNotEmpty() && type.isNotEmpty() && date.isNotEmpty() && montant.isNotEmpty()) {
                // Récupérer le LinearLayout parent où les transactions sont affichées
                val transactionsLayout = requireView().findViewById<LinearLayout>(R.id.transactions_layout)

                // Créer un nouveau LinearLayout pour représenter une transaction
                val newTransactionLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16)
                    }
                }

                // Ajouter le TextView pour le type
                val typeTextView = TextView(requireContext()).apply {
                    text = titre
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                // Ajouter le TextView pour la date
                val dateTextView = TextView(requireContext()).apply {
                    text = date
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Ajouter le TextView pour le montant
                val montantTextView = TextView(requireContext()).apply {
                    text = "$montant £"
                    textSize = 14f
                    setTextColor(resources.getColor(R.color.red, null))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = 16
                    }
                }

                // Ajouter les TextViews au LinearLayout de la transaction
                newTransactionLayout.addView(typeTextView)
                newTransactionLayout.addView(dateTextView)
                newTransactionLayout.addView(montantTextView)

                // Ajouter la nouvelle transaction au LinearLayout parent
                transactionsLayout.addView(newTransactionLayout)

                // Afficher un message de confirmation
                Toast.makeText(requireContext(), "Dépense ajoutée : $type, $date, $montant£", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonAnnuler.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("NewApi")
    private fun afficherFormulaireRevenu() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_ajouter_revenu, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_type_revenu)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.income_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val editType = dialogView.findViewById<EditText>(R.id.edit_type)
        val editDate = dialogView.findViewById<EditText>(R.id.edit_date)
        val editMontant = dialogView.findViewById<EditText>(R.id.edit_montant)
        val buttonAjouter = dialogView.findViewById<Button>(R.id.button_ajouter)
        val buttonAnnuler = dialogView.findViewById<Button>(R.id.button_annuler)

        buttonAjouter.setOnClickListener {
            val titre = editType.text.toString()
            val date = editDate.text.toString()
            val montant = editMontant.text.toString()
            val type = spinner.selectedItem.toString()

            if (titre.isNotEmpty() && type.isNotEmpty() && date.isNotEmpty() && montant.isNotEmpty()) {
                // Récupérer le LinearLayout parent où les transactions sont affichées
                val transactionsLayout = requireView().findViewById<LinearLayout>(R.id.transactions_layout)

                // Créer un nouveau LinearLayout pour représenter une transaction
                val newTransactionLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16)
                    }
                }

                // Ajouter le TextView pour le type
                val typeTextView = TextView(requireContext()).apply {
                    text = titre
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                // Ajouter le TextView pour la date
                val dateTextView = TextView(requireContext()).apply {
                    text = date
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Ajouter le TextView pour le montant
                val montantTextView = TextView(requireContext()).apply {
                    text = "$montant £"
                    textSize = 14f
                    setTextColor(resources.getColor(R.color.green, null))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = 16
                    }
                }

                // Ajouter les TextViews au LinearLayout de la transaction
                newTransactionLayout.addView(typeTextView)
                newTransactionLayout.addView(dateTextView)
                newTransactionLayout.addView(montantTextView)

                // Ajouter la nouvelle transaction au LinearLayout parent
                transactionsLayout.addView(newTransactionLayout)

                // Afficher un message de confirmation
                Toast.makeText(requireContext(), "Dépense ajoutée : $type, $date, $montant£", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
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