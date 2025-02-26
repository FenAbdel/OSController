package com.example.oscontroller.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.oscontroller.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StatistiqueFragement : Fragment() {

    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.graph_layout, container, false)
        barChart = view.findViewById(R.id.barChart)
        setupBarChart()
        // Fetch and display data from Firestore
        setDataToChart()
        return view
    }

    private fun setupBarChart() {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)

        // Configure X axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        // The label count will be adjusted dynamically based on the number of groups later
        // Configure Y axis
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.setLabelCount(6, true)

        barChart.axisRight.isEnabled = false
    }

    private fun setDataToChart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("StatistiqueFragment", "User not logged in")
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                // Use two maps to aggregate revenue and expense amounts by month (format "yyyy-MM")
                val revenueMap = mutableMapOf<String, Float>()
                val expenseMap = mutableMapOf<String, Float>()

                for (document in result) {
                    val transaction = document.data
                    val dateStr = transaction["date"] as? String ?: continue
                    // Extract year and month (e.g., "2025-02")
                    val monthKey = if (dateStr.length >= 7) dateStr.substring(0, 7) else dateStr
                    val amount = (transaction["amount"] as? Number)?.toFloat() ?: 0f
                    val type = transaction["is_a"] as? String ?: ""
                    if (type.equals("Revenu", ignoreCase = true)) {
                        revenueMap[monthKey] = (revenueMap[monthKey] ?: 0f) + amount
                    } else if (type.equals("Depence", ignoreCase = true)) {
                        expenseMap[monthKey] = (expenseMap[monthKey] ?: 0f) + amount
                    }
                }

                // Combine keys from both maps and sort them to get consistent groups
                val allMonths = (revenueMap.keys + expenseMap.keys).toList().sorted()
                val entriesRevenue = ArrayList<BarEntry>()
                val entriesExpense = ArrayList<BarEntry>()

                for ((index, month) in allMonths.withIndex()) {
                    entriesRevenue.add(BarEntry(index.toFloat(), revenueMap[month] ?: 0f))
                    entriesExpense.add(BarEntry(index.toFloat(), expenseMap[month] ?: 0f))
                }

                // Create the datasets for revenue and expense
                val setRevenue = BarDataSet(entriesRevenue, "Revenue").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.green)
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }
                val setExpense = BarDataSet(entriesExpense, "Expense").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.red)
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }

                // Configure bar grouping parameters
                val groupSpace = 0.4f
                val barSpace = 0.02f
                val barWidth = 0.3f

                val data = BarData(setRevenue, setExpense).apply {
                    this.barWidth = barWidth
                }

                barChart.data = data

                // Group the bars on the chart
                val groupCount = allMonths.size
                val startPosition = 0f
                data.groupBars(startPosition, groupSpace, barSpace)
                val xAxisMaximum = startPosition + data.getGroupWidth(groupSpace, barSpace) * groupCount
                barChart.xAxis.axisMinimum = startPosition
                barChart.xAxis.axisMaximum = xAxisMaximum

                // Optionally, you can set a custom value formatter on the X axis to show the month labels
                barChart.xAxis.valueFormatter = XAxisValueFormatter(allMonths)

                barChart.notifyDataSetChanged()
                barChart.invalidate()
            }
            .addOnFailureListener { exception ->
                Log.e("StatistiqueFragment", "Error fetching transactions: ${exception.message}")
            }
    }
}
