package com.example.oscontroller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.oscontroller.MainActivity
import com.example.oscontroller.R
import com.example.oscontroller.SignUp

class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.createaccount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use the view object to find the TextView
        val anchorTextView = view.findViewById<TextView>(R.id.anchorTextView)

        // Set a click listener
        anchorTextView.setOnClickListener {
            // Start the target activity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }


    }
}