package com.hercat.mevur.addbit.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.hercat.mevur.addbit.R
import com.hercat.mevur.data.Cost
import com.hercat.mevur.database.db
import kotlinx.android.synthetic.main.activity_analys.*

class AnalysActivity : AppCompatActivity() {

    val costs = mutableListOf<Cost>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analys)

        costs.clear()
        costs.addAll(db.selectByDay(today()))
        freshBarChart()
    }

    private fun freshBarChart() {
        val entris: MutableList<BarEntry> = mutableListOf()
        for ((index, cost) in costs.withIndex()) {
            val entry = BarEntry(index.toFloat(), cost.price.toFloat())
            entris.add(entry)
        }
        val barDataSet = BarDataSet(entris, "消费")

        barDataSet.isHighlightEnabled = true
        val barData = BarData(barDataSet)
        barChart.data = barData
        barData.barWidth = 0.5f

        barChart.legend.isEnabled = false
        barChart.setScaleEnabled(false)
        barChart.isDragEnabled = true
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setValueFormatter { value, _ ->
            costs[value.toInt()].datetime.substringAfter(" ").substringBeforeLast(":")
        }
        barChart.xAxis.granularity = 0.5f
        barChart.xAxis.labelCount = 10
        val left = barChart.axisLeft
        left.setDrawGridLines(false)
        val right = barChart.axisRight
        right.isEnabled = false
        barChart.invalidate()
    }
}
