package com.hercat.mevur.addbit.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.hercat.mevur.adapter.CostListAdapter
import com.hercat.mevur.addbit.R
import com.hercat.mevur.data.Cost
import com.hercat.mevur.data.totalMoney
import com.hercat.mevur.database.db
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: AppCompatActivity() {

    private val costs = mutableListOf<Cost>()
    private val adapter: CostListAdapter = CostListAdapter(costs, this)
    private var totalMoney: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_main)



        tickerView.setCharacterLists(TickerUtils.provideNumberList())
        supportActionBar?.hide()
        costList.adapter = adapter
        tickerView.text = "￥ 0.0"
        btnAdd.onClick(handler = { add() })

        costs.addAll(db.selectByDay(today()))
        adapter.notifyDataSetChanged()
        totalMoney = costs.totalMoney()
        tickerView.text = "￥ ${String.format("%.2f", totalMoney)}"

    }

    fun add() {

        fun getDateString(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            return simpleDateFormat.format(Date())
        }
        val str = edtPrice.text.toString()
        val money = try {
            if (str == "") 0.0 else str.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        if (money != 0.0) {
            val cost = Cost(datetime = getDateString(),
                    price = money,
                    tag = "")
            costs.add(cost)
            adapter.notifyDataSetChanged()
            totalMoney += money
            tickerView.text = "￥ ${String.format("%.2f", totalMoney)}"
            costList.setSelection(costList.bottom)
            //save to db
            val count = db.insertCost(cost)
            toast("$count")

        } else {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show()
        }
    }

    private fun today(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return simpleDateFormat.format(Date())
    }


}