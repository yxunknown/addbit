package com.hercat.mevur.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DataHelper private constructor(val context: Context,
                 val name: String,
                 val factory: SQLiteDatabase.CursorFactory?,
                 val version: Int): ManagedSQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private var helper: DataHelper? = null
        @Synchronized
        fun getInstance(context: Context, name: String,
                        factory: SQLiteDatabase.CursorFactory?, version: Int): DataHelper {
            if (helper == null) {
                helper = DataHelper(context.applicationContext, name, factory, version)
            }
            return helper!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("cost", true,
                "id" to INTEGER + PRIMARY_KEY,
                "date" to TEXT,
                "time" to  TEXT,
                "price" to TEXT,
                "tag" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}