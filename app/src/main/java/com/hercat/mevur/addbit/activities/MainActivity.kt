package com.hercat.mevur.addbit.activities

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.hercat.mevur.adapter.CostListAdapter
import com.hercat.mevur.addbit.R
import com.hercat.mevur.data.Cost
import com.hercat.mevur.data.totalMoney
import com.hercat.mevur.database.db
import com.rengwuxian.materialedittext.MaterialEditText
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
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

        btnHistory.onClick { startActivity(intentFor<AnalysActivity>()) }
        costList.adapter = adapter
        tickerView.text = "￥ 0.0"
        tvTodayDate.text = today()
        btnAdd.onClick(handler = { add() })
        costs.addAll(db.selectByDay(today()))
        adapter.notifyDataSetChanged()
        totalMoney = costs.totalMoney()
        tickerView.text = "￥ ${String.format("%.2f", totalMoney)}"
        tvCostCount.text = "今日记账 ${costs.size} 次"
        initDrawerList()
    }

    fun add() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_money, null)
        val etMoney = view.findViewById<MaterialEditText>(R.id.et_money)
        val wheelView = view.findViewById<WheelView>(R.id.wheelView)
        val tags = listOf("请选择标签", "餐饮", "购物", "消费")
        wheelView.setCyclic(false)
        wheelView.setTextColorCenter(resources.getColor(R.color.textPrimary))
        wheelView.setTextColorOut(resources.getColor(R.color.textSecondary))
        wheelView.setTextSize(15f)
        wheelView.setDividerColor(resources.getColor(R.color.colorDivider))
        wheelView.setOnItemSelectedListener { index: Int ->
            if (index == 0) {
                wheelView.setTextColorCenter(resources.getColor(R.color.colorDanger))
            } else {
                wheelView.setTextColorCenter(resources.getColor(R.color.textPrimary))
            }
        }
        wheelView.adapter = ArrayWheelAdapter(tags)
        fun validateDataInput(): Boolean {
            val result = etMoney.text.toString() != "" &&
                    wheelView.currentItem != 0
            if (etMoney.text.toString() == "") {
                etMoney.error = "没有填写金额"
            }
            if (wheelView.currentItem == 0) {
                toast("请选择标签")
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
                val tag = tags[wheelView.currentItem]
                val cost = Cost(datetime = getDateString(),
                        price = money,
                        tag = tag)
                costs.add(cost)
                adapter.notifyDataSetChanged()
                totalMoney += money
                tickerView.text = "￥ ${String.format("%.2f", totalMoney)}"
                tvCostCount.text = "今日记账 ${costs.size} 次"
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
        YoYo.with(Techniques.FadeInUp)
                .duration(500)
                .playOn(dialog.window.decorView)
    }

    private fun initDrawerList() {
        val funs = listOf("统计", "关于")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, funs)
        funList.adapter = adapter
        funList.setOnItemClickListener { parent, view, position, id ->
            when {
                (id == 0L) -> {
                    startActivity(intentFor<AnalysActivity>())
                }
                (id == 1L) -> {
                }
                else -> {

                }
            }
        }
    }


}