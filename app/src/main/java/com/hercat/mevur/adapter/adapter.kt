package com.hercat.mevur.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hercat.mevur.addbit.R
import com.hercat.mevur.data.Cost


fun BaseAdapter.getViewFromXml(resId: Int, parent: ViewGroup?, context: Context): View {
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(resId, parent)
}

class CostListAdapter(val data: List<Cost>,
                      val context: Context): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cost = data[position]
        val view = convertView ?: getViewFromXml(R.layout.item_list_cost, null, context)
        val tvId = view.findViewById<TextView>(R.id.tvId)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
        tvId.text = (position + 1).toString()
        tvTime.text = cost.datetime.substringBeforeLast(":").substringAfterLast(" ")
        tvPrice.text = "￥ ${cost.price}"
        return view
    }

    override fun getItem(p0: Int): Any = data[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = data.size

}