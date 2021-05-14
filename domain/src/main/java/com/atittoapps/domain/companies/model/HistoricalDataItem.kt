package com.atittoapps.domain.companies.model

data class HistoricalDataItem(
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Double
)