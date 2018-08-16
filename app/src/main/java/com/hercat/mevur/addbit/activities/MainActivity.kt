package com.hercat.mevur.addbit.activities

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
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
import com.rengwuxian.materialedittext.MaterialEditText
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.angmarch.views.NiceSpinner
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

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

        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_money, null)
        val etMoney = view.findViewById<MaterialEditText>(R.id.et_money)
        val etTag = view.findViewById<MaterialEditText>(R.id.et_tag)

        fun validateDataInput(): Boolean {
            val result = etMoney.text.toString() != "" &&
                    etTag.text.toString() != "" &&
                    etTag.text.toString().length <= 10
            if (etMoney.text.toString() == "") {
                etMoney.error = "没有填写金额"
            }
            if (etTag.text.toString() == "") {
                etTag.error = "输入一下描述阿"
            }
            if (etTag.text.toString().length > 10) {
                etTag.error = "你输入的描述太长了"
            }
            return result

        }

        fun getDateString(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            return simpleDateFormat.format(Date())
        }

        view.findViewById<View>(R.id.positive).setOnClickListener { _ ->
            if (validateDataInput()) {
                val money = etMoney.text.toString().toDouble()
                val tag = etTag.text.toString()
                val cost = Cost(datetime = getDateString(),
                        price = money,
                        tag = tag)
                costs.add(cost)
                adapter.notifyDataSetChanged()
                totalMoney += money
                tickerView.text = "￥ ${String.format("%.2f", totalMoney)}"
                costList.setSelection(costList.bottom)
                //save to db
                db.insertCost(cost)
                dialog.dismiss()
            }
        }
        view.findViewById<View>(R.id.negative).setOnClickListener { _ -> dialog.dismiss() }
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun today(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return simpleDateFormat.format(Date())
    }


}