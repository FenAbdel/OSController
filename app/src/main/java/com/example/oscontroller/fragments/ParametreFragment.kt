package com.example.oscontroller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.oscontroller.R

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

        return view

    }


}