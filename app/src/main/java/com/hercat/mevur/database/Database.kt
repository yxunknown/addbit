package com.hercat.mevur.database

import android.content.ContentValues
import android.content.Context
import com.hercat.mevur.data.Cost
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.select

class Database(val context: Context) {
    private val DB_NAME = "COST_YOUR_MONEY"
    private val VERSION = 1
    private var db: DataHelper = DataHelper.getInstance(context, DB_NAME, null, VERSION)


    fun insertCost(cost: Cost): Long {
        val values = ContentValues()
        values.put("date", cost.datetime.substringBeforeLast(" "))
        values.put("time", cost.datetime.substringAfterLast(" "))
        values.put("price", cost.price)
        values.put("tag", cost.tag)
        values.put("id", System.nanoTime())
        return db.use {
            insert("cost", null, values)
        }
    }

    /**
     * [date] format as yyyy-MM-dd
     */
    fun selectByDay(date: String): List<Cost> {
        return db.use { select("cost")
                .whereArgs("date={date}", "date" to date)
                .parseList(CostParser)
        }
    }

    fun selectByMonth(month: String): List<Cost> {
        return db.use {
            select("cost")
                    .whereArgs("date LIKE {date}%", "date" to month)
                    .parseList(CostParser)
        }

    }

    fun selectByYear(year: String): List<Cost> {
        return db.use {
            select("cost")
                    .whereArgs("date LIKE {date}%", "date" to year)
                    .parseList(CostParser)
        }
    }

    object CostParser: RowParser<Cost> {
        override fun parseRow(columns: Array<Any?>): Cost {
            return Cost(id = columns[0] as Long,
                    datetime = "${columns[1]} ${columns[2]}",
                    price = (columns[3] as String).toDouble(),
                    tag = columns[4] as String)
        }
    }

}

//definition database for activity

val Context.db: Database
    get() = Database(this)