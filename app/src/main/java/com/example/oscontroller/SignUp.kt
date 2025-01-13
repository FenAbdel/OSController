package com.example.oscontroller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.oscontroller.fragments.SignUpFragment


class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        val backsignupButton = findViewById<ImageButton>(R.id.backsigninbutton)
        backsignupButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.signupFragment_container, SignUpFragment())
        transaction.addToBackStack(null)
        transaction.commit()

    }
}