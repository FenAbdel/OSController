package com.example.oscontroller.fragments

import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val months: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < months.size) months[index] else ""
    }
}
