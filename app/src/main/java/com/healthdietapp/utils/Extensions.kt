package com.healthdietapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.showErrorSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(context.getColor(com.healthdietapp.R.color.error))
        .setTextColor(context.getColor(android.R.color.white))
        .show()
}
