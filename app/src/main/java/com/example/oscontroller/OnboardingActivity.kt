package com.example.oscontroller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.oscontroller.fragments.OnboardingAdapter
import com.example.oscontroller.fragments.OnboardingItem

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var buttonNext: Button
    private lateinit var textSkip: TextView
    private lateinit var dotsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        buttonNext = findViewById(R.id.buttonNext)
        textSkip = findViewById(R.id.textSkip)
        dotsLayout = findViewById(R.id.layoutDots)

        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.on1
            ),
            OnboardingItem(
                R.drawable.on2
            )
        )

        viewPager.adapter = OnboardingAdapter(onboardingItems)
        setupDots()
        setCurrentDot(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentDot(position)
                buttonNext.text = if (position == onboardingItems.size - 1) "Get Started" else "Next"
            }
        })

        buttonNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else {
                startMainActivity()
            }
        }

        textSkip.setOnClickListener {
            startMainActivity()
        }
    }

    private fun setupDots() {
        val dots = arrayOfNulls<ImageView>(3)
        dotsLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageResource(R.drawable.dot_inactive)
            dotsLayout.addView(dots[i])
        }
    }

    private fun setCurrentDot(position: Int) {
        val childCount = dotsLayout.childCount
        for (i in 0 until childCount) {
            val imageView = dotsLayout.getChildAt(i) as ImageView
            imageView.setImageResource(
                if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    private fun startMainActivity() {
        // Mark onboarding as completed
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("onboarding_completed", true).apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}