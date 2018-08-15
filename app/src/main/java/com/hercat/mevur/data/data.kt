package com.hercat.mevur.data

data class Cost(val id: Long = 1,
                val datetime: String,
                val price: Double,
                val tag: String)

fun List<Cost>.totalMoney(): Double {
    var total = 0.0
    forEach { cost -> total += cost.price }
    return total
}