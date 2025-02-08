package com.example.oscontroller.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.oscontroller.MainActivity
import com.example.oscontroller.R
import com.google.firebase.auth.FirebaseAuth

class ParametreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_settings, container, false)

        // Find the Spinner in the layout
        val deviseSpinner: Spinner = view.findViewById(R.id.devise_spinner)

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


}