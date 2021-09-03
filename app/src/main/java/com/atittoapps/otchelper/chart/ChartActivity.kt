package com.atittoapps.otchelper.chart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.common.BiggestChartView
import com.atittoapps.otchelper.common.HistoricalDataItemParcelable

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        val chart = findViewById<BiggestChartView>(R.id.biggestChartView)
        val items = intent.getParcelableArrayListExtra<HistoricalDataItemParcelable>(LIST_ITEMS)
        chart.historicalData = items?.map { it.toDomain() } ?: listOf()
    }

    companion object {
        const val LIST_ITEMS = "items"
    }
}