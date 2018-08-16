package com.hercat.mevur.addbit.activities

import android.app.Activity
import android.support.design.widget.Snackbar
import android.widget.Toast

fun Activity.snackbar(message: String) {
    Snackbar.make(window.peekDecorView(), message, Toast.LENGTH_SHORT).show()
}

