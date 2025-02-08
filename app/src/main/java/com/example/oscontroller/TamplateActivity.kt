package com.example.oscontroller

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.oscontroller.fragments.AuthFragment
import com.example.oscontroller.fragments.HistoriqueFragment
import com.example.oscontroller.fragments.HomeFragment
import com.example.oscontroller.fragments.ParametreFragment
import com.example.oscontroller.fragments.StatistiqueFragement
import com.google.android.material.bottomnavigation.BottomNavigationView

class TamplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tamplate)

        val navigrationView = findViewById<BottomNavigationView>(R.id.navigration_view)

        navigrationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.paramertre_page -> {
                    loadFragment(ParametreFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.home_page -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.historic_page -> {
                    loadFragment(HistoriqueFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.statistics_page -> {
                    loadFragment(StatistiqueFragement())
                    return@setOnNavigationItemSelectedListener true
                }

                else -> false
            }
        }
        // Load HomeFragment by default
        loadFragment(HomeFragment())
        // Set the selected item in bottom navigation to home
        navigrationView.selectedItemId = R.id.home_page
    }

    private fun loadFragment(parametreFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_content_container, parametreFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}