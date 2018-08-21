package com.hercat.mevur.addbit.activities

import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.hercat.mevur.addbit.R
import com.hercat.mevur.data.Cost
import com.hercat.mevur.database.db
import kotlinx.android.synthetic.main.activity_analys.*
import java.text.SimpleDateFormat
import java.util.*

class AnalysActivity : AppCompatActivity() {

    private val TYPE_DAY = 0
    private val TYPE_MONTH = 1
    private val TYPE_YEAR = 2
    var type: Int = TYPE_DAY
    val date: Calendar = Calendar.getInstance()
    var currentSelect = 0;

    val costs = mutableListOf<Cost>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analys)
        tvTitle.text = today()
        btnDate.setOnClickListener { _ ->
            val timePicker = TimePickerBuilder(this) { date, _ ->
                                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                        freshBarChart(simpleDateFormat.format(date))
                    }
                            .setDate(date)
                            .setType(getType().toBooleanArray())
                            .setSubmitText("确认")
                            .setCancelText("取消")
                            .isDialog(true)
                            .build()
                    timePicker.show()
        }
        btnType.setOnClickListener { v ->
            MaterialDialog(this)
                            .listItemsSingleChoice(items = listOf("按天统计", "按月统计", "按年统计"),
                                    initialSelection = currentSelect) {dialog, index, text ->
                                currentSelect = index
                                val icon = when(index) {
                                    0 -> R.drawable.day
                                    1 -> R.drawable.month
                                    2 -> R.drawable.year
                                    else -> 0
                                }
                                btnType.setImageResource(icon)
                                freshBarChart(selection = today())
                            }
                            .show()
        }
        btnBack.setOnClickListener { onBackPressed() }
        initBarChart()
        freshBarChart(today())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_analys, menu)
        return true
    }

    private fun freshBarChart(selection: String) {

        tvTitle.text = when(currentSelect) {
            0 -> selection
            1 -> selection.substringBeforeLast("-")
            2 -> selection.substringBefore("-")
            else -> ""
        }

        date.time = with(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)) {
            parse(selection)
        }
        val currentCost = when(currentSelect) {
            0 -> db.selectByDay(selection)
            1 -> {
                db.selectByMonth(month = selection.substringBeforeLast("-"))
                        .asSequence()
                        .groupBy { it.datetime.substringBefore(" ") }
                        .map { Cost(datetime = it.key,
                                price = it.value.sumByDouble { it.price },
                                tag = "日消费") }
                        .toList()
            }
            2 -> {
                db.selectByYear(year = selection.substringBefore("-"))
                        .asSequence()
                        .groupBy { it.datetime.substringBefore("-") }
                        .map { Cost(datetime = it.key,
                                price = it.value.sumByDouble { it.price },
                                tag = "") }
                        .toList()
            }
            else -> listOf()
        }
        costs.clear()
        costs.addAll(currentCost)
        if (costs.size == 0) {
            barChart.clear()
            barChart.notifyDataSetChanged()
            barChart.setNoDataText("没有记账数据")
            barChart.setNoDataTextColor(resources.getColor(R.color.colorPrimary))
            // 记得最后要刷新一下
            barChart.invalidate()
        } else {
            barChart.clear()
            barChart.description.text = tvTitle.text.toString() + " 消费统计  消费总金额:￥${costs.sumByDouble { it.price }}"
            val entris: MutableList<BarEntry> = mutableListOf()
            for ((index, cost) in costs.withIndex()) {
                val entry = BarEntry(index.toFloat(), cost.price.toFloat())
                entris.add(entry)
            }
            val barDataSet = BarDataSet(entris, "消费")
            barDataSet.isHighlightEnabled = true
            barDataSet.color = Color.parseColor("#5ab1ef")
            barDataSet.highLightColor = Color.parseColor("#59678c")
            val barData = BarData(barDataSet)
            barData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                value.toString() + "元"
            }
            barData.setValueTextColor(Color.parseColor("#0098d9"))
            barData.barWidth = 0.5f
            barChart.data = barData


            val labels = costs.asSequence()
                    .map {
                        when(currentSelect) {
                            0 -> it.datetime.substringAfter(" ")
                                    .substringBeforeLast(":")
                            1 -> selection.substringAfter("-")
                            2 -> selection.substringBeforeLast("-")
                            else -> ""
                        }
                    }
                    .toList()
            barChart.xAxis.setValueFormatter { value, _ ->
                val index = value.toInt()
                if (value < 0 || value >= labels.size) {
                    ""
                } else {
                    labels[index]
                }
            }
            fun getMatrix(sx: Float, sy: Float = 1.0f): Matrix {
                val m = Matrix()
                m.postScale(sx, sy)
                return m
            }

            val matrix: Matrix = when {
                (labels.size <= 10) -> getMatrix(labels.size / 10f)
                (labels.size <= 20) -> getMatrix(2.0f)
                (labels.size <= 30) -> getMatrix(3.0f)
                (labels.size <= 40) -> getMatrix(4.0f)
                else -> getMatrix(5.0f)
            }
            barChart.viewPortHandler.refresh(matrix, barChart, false)
            // x轴执行动画
            barChart.animateXY(1000, 1000)
            barChart.invalidate()
        }

    }

    private fun initBarChart() {
        barChart.setDrawGridBackground(false)

        barChart.legend.isEnabled = false
        barChart.description.isEnabled = true
        barChart.description.setPosition(480f, 50f)
        barChart.description.textColor = resources.getColor(R.color.colorPrimary)
        barChart.setScaleEnabled(true)
        barChart.isDragEnabled = true

        // x轴相关
        val xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.yOffset = 0.0f
        xAxis.granularity = 1.0f
        //y轴相关
        val left = barChart.axisLeft
        left.setDrawGridLines(false)
        left.setDrawAxisLine(false)
        left.setDrawZeroLine(true)
        left.textSize = 12f
        left.granularity = 5.0f
        left.xOffset = 12.0f
        val right = barChart.axisRight
        right.isEnabled = false
    }

    private fun getType(): Array<Boolean> {
        return when(currentSelect) {
            0 -> arrayOf(true, true, true, false, false, false)
            1 -> arrayOf(true, true, false, false, false, false)
            2 -> arrayOf(true, false, false, false, false, false)
            else -> arrayOf(true, true, true, true, true, true)
        }
    }
}
