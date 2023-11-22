package dev.lazygarde.watering.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyAxisFormatter(private val firstTime: Long) : IndexAxisValueFormatter() {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val timestamp = firstTime + value.toLong() * 1000
        return dateFormat.format(Date(timestamp))
    }
}
