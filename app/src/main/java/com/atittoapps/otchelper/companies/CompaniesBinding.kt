package com.atittoapps.otchelper.companies

import android.content.Context
import android.graphics.Paint
import android.text.Html
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atitto.mviflowarch.extensions.gone
import com.atitto.mviflowarch.extensions.visible
import com.atitto.mviflowarch.extensions.visibleIfOrGone
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.HistoricalDataItem
import com.atittoapps.otchelper.MainApplication
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.common.ChartView
import com.atittoapps.otchelper.filter.Filters
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object CompaniesBinding {

    private val formatter = SimpleDateFormat("dd.MM.yyyy")

    fun getDate(date: Long) =
            formatter.format(Date(date))

    @JvmStatic
    @BindingAdapter("visibleIfOrGone")
    fun viewVisibleIfOrGone(view: View, flag: Boolean) {
        view.visibleIfOrGone { flag }
    }

    @JvmStatic
    @BindingAdapter("isActive")
    fun isActiveImage(view: ImageView, flag: Boolean) {
        view.alpha = if (flag) 1.0f else 0.2f
    }

    @JvmStatic
    @BindingAdapter("isActualMoreThanFirst")
    fun isActualMoreThanFirst(view: TextView, stock: DomainStock) {
        val first = stock.priceFirst ?: 0.0
        val actual = stock.lastSale ?: 0.0
        view.setTextColor(MainApplication.resolveColorFromAttr(if (actual > first) R.attr.colorGood else if (actual == first) R.attr.colorOnBackground else R.attr.colorAlert))
    }

    @JvmStatic
    @BindingAdapter("fromHtml")
    fun isActualMoreThanFirst(view: TextView, stock: String) {
        view.text = Html.fromHtml(stock)
    }

    private val format = DecimalFormat("##0.####")

    @JvmStatic
    @BindingAdapter("doubleValue")
    fun isActualMoreThanFirst(view: TextView, value: Double?) {
        view.text = value?.let { format.format(it).replace(",", ".") }
    }

    @JvmStatic
    @BindingAdapter("lastMax")
    fun fromLastMax(view: TextView, value: Double?) {
        val valueNotNull = value ?: return
        view.text = view.context.getString(R.string.from_prev_max).format(valueNotNull * 100, "%")
    }

    @JvmStatic
    @BindingAdapter("items")
    fun fromLastMax(view: RecyclerView, value: DomainStock) {

    }

    @JvmStatic
    @BindingAdapter("historicalData")
    fun historicalData(view: CandleStickChart, historicalData: List<HistoricalDataItem>) {
        if (historicalData.isEmpty()) {
            view.gone()
            return
        }
        view.visible()
        view.requestDisallowInterceptTouchEvent(true)

        val xAxis: XAxis = view.getXAxis()
        val yAxis: YAxis = view.axisLeft
        val yRightAxis = view.axisRight

        xAxis.setDrawGridLines(false) // disable x axis grid lines

        xAxis.setDrawLabels(false)
        yAxis.setDrawLabels(false)
        //yRightAxis.setDrawLabels(false)
        yRightAxis.maxWidth = dpToPixels(view.context, 10f)
        yRightAxis.minWidth = dpToPixels(view.context, 10f)
        yRightAxis.textSize = spToPixels(view.context, 2f)
        yRightAxis.textColor = MainApplication.resolveColorFromAttr(R.attr.colorOnBackground)
        yRightAxis.setLabelCount(6, false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)
        view.minOffset = 0f
        view.description.isEnabled = false
        view.setMaxVisibleValueCount(200)

        view.setTouchEnabled(false)
        view.setScaleEnabled(false)

        val l: Legend = view.getLegend()
        l.isEnabled = false
        l.mNeededWidth = dpToPixels(view.context, 50f)
        val set = historicalData.mapIndexed { index, historicalDataItem ->
            CandleEntry(
                index.toFloat(),
                historicalDataItem.high.toFloat(),
                historicalDataItem.low.toFloat(),
                historicalDataItem.open.toFloat(),
                historicalDataItem.close.toFloat()
            )
        }
        val dataset = CandleDataSet(set, "DataSet 1")
        with(dataset) {
            decreasingColor = MainApplication.resolveColorFromAttr(R.attr.colorAlert)
            decreasingPaintStyle = Paint.Style.FILL

            increasingColor = MainApplication.resolveColorFromAttr(R.attr.colorGood)
            increasingPaintStyle = Paint.Style.FILL

            shadowColor = MainApplication.resolveColorFromAttr(R.attr.colorOnBackground)
            shadowWidth = dpToPixels(view.context, 0.1f)
            setDrawValues(false)
        }
        val data = CandleData(dataset)
        view.data = data
        view.invalidate()
    }

    @JvmStatic
    @BindingAdapter("historicalData")
    fun historicalData(view: ChartView, historicalData: List<HistoricalDataItem>) {
        if (historicalData.isEmpty()) {
            view.gone()
            return
        }
        view.visible()
        view.historicalData = historicalData
    }

    @JvmStatic
    @BindingAdapter("historicalData")
    fun historicalData(view: BarChart, historicalData: List<HistoricalDataItem>) {
        if (historicalData.isEmpty()) {
            view.gone()
            return
        }
        view.visible()
        view.requestDisallowInterceptTouchEvent(true)
        view.setTouchEnabled(false)
        view.setScaleEnabled(false)
        view.setMaxVisibleValueCount(200)

        val xAxis: XAxis = view.getXAxis()
        val yAxis: YAxis = view.axisLeft
        val yRightAxis = view.axisRight

        xAxis.setDrawGridLines(false) // disable x axis grid lines

        xAxis.setDrawLabels(false)
        yAxis.setDrawLabels(false)
        //yRightAxis.setDrawLabels(false)
        yRightAxis.maxWidth = dpToPixels(view.context, 10f)
        yRightAxis.minWidth = dpToPixels(view.context, 10f)
        yRightAxis.textSize = spToPixels(view.context, 2f)
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
        view.minOffset = 0f
        view.description.isEnabled = false

        val l: Legend = view.getLegend()
        l.isEnabled = false
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
        view.data = data
        view.invalidate()
    }

    @JvmStatic
    @BindingAdapter("app:range")
    fun setRange(seekbar: CrystalRangeSeekbar, filter: Filters.RangeShares) {
        val maxMax = filter.rangeMax.max ?: 100
        val maxMin = filter.rangeMax.min ?: 0
        val currentMax = filter.rangeCurrent.max ?: 100
        val currentMin = filter.rangeCurrent.min ?: 0
        seekbar.setMaxValue(maxMax.toFloat())
        seekbar.setMinValue(maxMin.toFloat())
        seekbar.setMaxStartValue(currentMax.toFloat())
        seekbar.setMinStartValue(currentMin.toFloat())
        seekbar.apply()
    }

    fun spToPixels(context: Context, spValue: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics)
    }

    fun dpToPixels(context: Context, dpValue: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics)
    }

}