package com.example.oscontroller.fragments

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.oscontroller.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Date


class HistoriqueFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.hystorique_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bouton pour afficher le formulaire de dépense



    }

data class Transaction(
    val title: String = "",
    val amount: Double = 0.0,
    val date: Date = Date(),
    val type: String = ""
)

}