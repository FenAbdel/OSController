package com.example.oscontroller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.oscontroller.MainActivity
import com.example.oscontroller.R
import com.example.oscontroller.SignUp
import com.example.oscontroller.TamplateActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var checkBox: CheckBox


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.createaccount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Find views
        firstNameEditText = view.findViewById(R.id.createaccount_prenom)
        lastNameEditText = view.findViewById(R.id.createaccount_nom)
        emailEditText = view.findViewById(R.id.createaccount_email)
        passwordEditText = view.findViewById(R.id.createaccount_password)
        confirmPasswordEditText = view.findViewById(R.id.createaccount_password_confirmation)
        createAccountButton = view.findViewById(R.id.button4)
        checkBox = view.findViewById(R.id.condition_checkbox)
        val anchorTextView = view.findViewById<TextView>(R.id.anchorTextView)

        // Set click listeners
        anchorTextView.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        createAccountButton.setOnClickListener {
            createAccount()
        }
    }
    private fun createAccount() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkBox.isChecked) {
            Toast.makeText(
                requireContext(),
                "Please accept terms and conditions",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { firebaseUser ->
                        // Create Firestore document with user's UID
                        val db = FirebaseFirestore.getInstance()
                        val userDocument = db.collection("users").document(firebaseUser.uid)

                        // Prepare user data
                        val userData = hashMapOf(
                            "uid" to firebaseUser.uid,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis(),
                            "hasCompletedOnboarding" to false
                        )

                        // Save user data to Firestore
                        userDocument.set(userData)
                            .addOnSuccessListener {
                                // Update user profile with display name
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName("$firstName $lastName")
                                    .build()

                                firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            // Navigate to main activity
                                            val intent = Intent(requireContext(), MainActivity::class.java)
                                            startActivity(intent)
                                            requireActivity().finish()
                                        }
                                    }

                                Toast.makeText(
                                    requireContext(),
                                    "Account created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to create user profile: ${e.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // Authentication failed
                    Toast.makeText(
                        requireContext(),
                        "Registration failed: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
}