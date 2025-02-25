package com.example.oscontroller.fragments

import android.graphics.Color
import android.os.Bundle
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

class StatistiqueFragement : Fragment() { // Fixed typo in class name

    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.graph_layout, container, false)

        // Initialize views using the inflated view
        barChart = view.findViewById(R.id.barChart)

        setupBarChart()
        setDataToChart()

        return view
    }

    private fun setupBarChart() {
        // Configure chart appearance
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
        xAxis.labelCount = 7

        // Configure Y axis
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.setLabelCount(6, true)

        barChart.axisRight.isEnabled = false
    }

    private fun setDataToChart() {
        // Sample data - replace with your actual data
        val revenues = listOf(5000f, 6000f, 5500f, 7000f, 8000f)
        val expenses = listOf(4000f, 4500f, 5000f, 6000f, 6500f)

        val entriesRevenue = ArrayList<BarEntry>()
        val entriesExpense = ArrayList<BarEntry>()

        // Create entries
        for (i in revenues.indices) {
            entriesRevenue.add(BarEntry(i.toFloat(), revenues[i]))
            entriesExpense.add(BarEntry(i.toFloat(), expenses[i]))
        }

        // Create datasets
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

        val groupSpace = 0.4f
        val barSpace = 0.02f
        val barWidth = 0.3f

        val data = BarData(setRevenue, setExpense).apply {
            this.barWidth = barWidth
        }

        barChart.data = data

        // Modified group bars calculation
        val groupCount = revenues.size.toFloat()
        val startPosition = 0f
        data.groupBars(startPosition, groupSpace, barSpace)

        // Calculate axis maximum manually if needed
        val xAxisMaximum = startPosition + data.getGroupWidth(groupSpace, barSpace) * groupCount
        barChart.xAxis.axisMinimum = startPosition
        barChart.xAxis.axisMaximum = xAxisMaximum

        barChart.invalidate()
    }
}