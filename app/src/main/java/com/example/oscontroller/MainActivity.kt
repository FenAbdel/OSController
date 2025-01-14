package com.example.oscontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.oscontroller.fragments.AuthFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, AuthFragment())
            transaction.addToBackStack(null)
            transaction.commit()


    }
}

