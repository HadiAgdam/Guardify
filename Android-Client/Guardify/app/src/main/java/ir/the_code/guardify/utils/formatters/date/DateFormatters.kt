package ir.the_code.guardify.utils.formatters.date

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun Long.formatMonthAndYear() = SimpleDateFormat("MMMM yyyy").format(this) ?: ""

@SuppressLint("SimpleDateFormat")
fun Long.formatTime() = SimpleDateFormat("HH:mm").format(this) ?: ""

@SuppressLint("SimpleDateFormat")
fun Long.formatDateTime() = SimpleDateFormat("MMM dd - HH:mm").format(this) ?: ""