package com.atittoapps.otchelper.common

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.atittoapps.domain.companies.model.HistoricalDataItem
import com.atittoapps.otchelper.MainApplication
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.companies.CompaniesBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlin.math.roundToInt

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var historicalData: List<HistoricalDataItem> = listOf()
        set(value) {
            field = value
            handleHistoricalData(value)
        }

    init {
        inflate(context, R.layout.view_historical_chart, this)

    }

    var candlesListener: OnChartValueSelectedListener? = null
    var volumeListener: OnChartValueSelectedListener? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val candlesChart = findViewById<CandleStickChart>(R.id.candle_stick_chart)
        val volumeChart = findViewById<BarChart>(R.id.volume_chart)

        candlesListener = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e ?: return
                h ?: return
                volumeChart.highlightValue(Highlight(e.x, 0, 0))
                dealWithSelected(e.x)
            }

            override fun onNothingSelected() {
            }

        }

        volumeListener =  object: OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e ?: return
                h ?: return
                candlesChart.highlightValue(Highlight(e.x, 0, 0))
                dealWithSelected(e.x)
            }

            override fun onNothingSelected() {
            }

        }

        initCharts()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        candlesListener = null
        volumeListener = null

    }

    private fun handleHistoricalData(historicalData: List<HistoricalDataItem>) {
        val candlesChart = findViewById<CandleStickChart>(R.id.candle_stick_chart)
        val volumeChart = findViewById<BarChart>(R.id.volume_chart)

        val set = historicalData.filter { it.volume >= 0 }.mapIndexed { index, historicalDataItem ->
            BarEntry(
                index.toFloat(),
                historicalDataItem.volume.toFloat()
            )
        }
        val dataset = BarDataSet(set, "DataSet 1")
        with(dataset) {
            setDrawValues(false)
        }
        val data = BarData(dataset)
        volumeChart.data = data
        volumeChart.invalidate()

        val setCandles = historicalData.mapIndexed { index, historicalDataItem ->
            CandleEntry(
                index.toFloat(),
                historicalDataItem.high.toFloat(),
                historicalDataItem.low.toFloat(),
                historicalDataItem.open.toFloat(),
                historicalDataItem.close.toFloat()
            )
        }
        val datasetCandles = CandleDataSet(setCandles, "DataSet 1")
        with(datasetCandles) {
            decreasingColor = MainApplication.resolveColorFromAttr(R.attr.colorAlert)
            decreasingPaintStyle = Paint.Style.FILL

            increasingColor = MainApplication.resolveColorFromAttr(R.attr.colorGood)
            increasingPaintStyle = Paint.Style.FILL

            shadowColor = MainApplication.resolveColorFromAttr(R.attr.colorOnBackground)
            shadowWidth = CompaniesBinding.dpToPixels(context, 0.1f)
            setDrawValues(false)
        }
        val dataCandles = CandleData(datasetCandles)
        candlesChart.data = dataCandles
        candlesChart.invalidate()
    }

    private fun dealWithSelected(position: Float) {
        val index = position.toInt()
        val tvOpen = findViewById<TextView>(R.id.open)
        val tvClose = findViewById<TextView>(R.id.close)
        val tvHigh = findViewById<TextView>(R.id.high)
        val tvLow = findViewById<TextView>(R.id.low)
        val tvVol = findViewById<TextView>(R.id.vol)

        CompaniesBinding.isActualMoreThanFirst(tvOpen, historicalData.getOrNull(index)?.open)
        CompaniesBinding.isActualMoreThanFirst(tvClose, historicalData.getOrNull(index)?.close)
        CompaniesBinding.isActualMoreThanFirst(tvHigh, historicalData.getOrNull(index)?.high)
        CompaniesBinding.isActualMoreThanFirst(tvLow, historicalData.getOrNull(index)?.low)
        tvVol.text = historicalData.getOrNull(index)?.volume?.let { getFormattedValue(it.toFloat()) }
    }

    fun getFormattedValue(value: Float): String {
        if (value == 0f) return ""
        if (value < 0f) return ""
        if (value >= 1000000000) return "${value / 1000000000}B"
        if (value >= 1000000) return "${(value / 1000000).roundToInt()}M"
        if (value >= 1000) return "{${(value / 1000).roundToInt()}K}"
        return value.roundToInt().toString()
    }

    private fun initCharts() {
        val candlesChart = findViewById<CandleStickChart>(R.id.candle_stick_chart)
        val volumeChart = findViewById<BarChart>(R.id.volume_chart)

        with(volumeChart) {
            requestDisallowInterceptTouchEvent(true)

            setScaleEnabled(false)
            setMaxVisibleValueCount(200)

            val xAxis: XAxis = xAxis
            val yAxis: YAxis = axisLeft
            val yRightAxis = axisRight

            setOnChartValueSelectedListener(volumeListener)

            xAxis.setDrawGridLines(false) // disable x axis grid lines

            xAxis.setDrawLabels(false)
            yAxis.setDrawLabels(false)
            yRightAxis.maxWidth = CompaniesBinding.dpToPixels(context, 10f)
            yRightAxis.minWidth = CompaniesBinding.dpToPixels(context, 10f)
            yRightAxis.textSize = CompaniesBinding.spToPixels(context, 2f)
            yRightAxis.textColor = MainApplication.resolveColorFromAttr(R.attr.colorOnBackground)
            yRightAxis.valueFormatter = object : IAxisValueFormatter {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    axis?.setLabelCount(7, false)
                    if (value == 0f) return ""
                    if (value < 0f) return ""
                    if (value >= 1000000000) return "${value / 1000000000}B"
                    if (value >= 1000000) return "${(value / 1000000).roundToInt()}M"
                    if (value >= 1000) return "${(value / 1000).roundToInt()}K"
                    return value.roundToInt().toString()
                }
            }
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setAvoidFirstLastClipping(true)
            minOffset = 0f
            description.isEnabled = false

            val l: Legend = legend
            l.isEnabled = false
        }

        with(candlesChart) {
            requestDisallowInterceptTouchEvent(true)

            val xAxis: XAxis = getXAxis()
            val yAxis: YAxis = axisLeft
            val yRightAxis = axisRight

            setOnChartValueSelectedListener(candlesListener)

            xAxis.setDrawGridLines(false) // disable x axis grid lines

            xAxis.setDrawLabels(false)
            yAxis.setDrawLabels(false)
            //yRightAxis.setDrawLabels(false)
            yRightAxis.maxWidth = CompaniesBinding.dpToPixels(context, 10f)
            yRightAxis.minWidth = CompaniesBinding.dpToPixels(context, 10f)
            yRightAxis.textSize = CompaniesBinding.spToPixels(context, 2f)
            yRightAxis.textColor = MainApplication.resolveColorFromAttr(R.attr.colorOnBackground)
            yRightAxis.setLabelCount(6, false)
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setAvoidFirstLastClipping(true)
            minOffset = 0f
            description.isEnabled = false
            setMaxVisibleValueCount(200)
            setScaleEnabled(false)

            val l: Legend = legend
            l.isEnabled = false
        }

    }



}