package com.hercat.mevur.addbit.activities

import android.app.Activity
import android.support.design.widget.Snackbar
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Activity.snackbar(message: String) {
    Snackbar.make(window.peekDecorView(), message, Toast.LENGTH_SHORT).show()
}
fun Activity.today(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return simpleDateFormat.format(Date())
}

