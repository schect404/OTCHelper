package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.HistoricalDataItem
import com.google.gson.annotations.SerializedName

data class HistoricalData(
    @SerializedName("o")
    val opens: List<Double> = listOf(),
    @SerializedName("c")
    val closes: List<Double> = listOf(),
    @SerializedName("h")
    val highs: List<Double> = listOf(),
    @SerializedName("l")
    val lows: List<Double> = listOf(),
    @SerializedName("v")
    val volumes: List<Double> = listOf()
) {

    fun toDataItemList() =  opens?.mapIndexed { index, value ->
        HistoricalDataItem(
            open = value,
            close = closes?.get(index),
            high = highs?.get(index),
            low = lows?.get(index),
            volume = volumes?.get(index)
        )
    }

}