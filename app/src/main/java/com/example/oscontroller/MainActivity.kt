package com.example.oscontroller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.oscontroller.fragments.AuthFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val onboardingCompleted = sharedPref.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // Start onboarding activity and finish current
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        } else {
            // Show authentication fragment if onboarding is completed
            showAuthFragment()
        }
    }

    private fun showAuthFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, AuthFragment())
            commit()
        }
    }
}

