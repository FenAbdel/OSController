package com.example.oscontroller.fragments

import android.app.AlertDialog
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oscontroller.R
import com.example.oscontroller.SignUp
import com.example.oscontroller.TamplateActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var forgotPasswordTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.authentification, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Find views
        emailEditText = view.findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        val signupButton = view.findViewById<Button>(R.id.signupButton)
        val signinButton = view.findViewById<Button>(R.id.signinButton)





        signupButton.setOnClickListener {
            val intent = Intent(requireContext(), SignUp::class.java)
            startActivity(intent)
        }

        signinButton.setOnClickListener {
            signInWithEmailAndPassword()
        }


        forgotPasswordTextView = view.findViewById(R.id.textView6)
        forgotPasswordTextView.setOnClickListener {
            showPasswordResetDialog()
        }
    }

    private fun signInWithEmailAndPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(requireContext(), TamplateActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showPasswordResetDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_password_reset, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.resetEmailEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Réinitialiser le mot de passe")
            .setView(dialogView)
            .setPositiveButton("Envoyer") { _, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(requireContext(), "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
            .show()
    }

    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Un e-mail de réinitialisation a été envoyé à $email",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erreur: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}