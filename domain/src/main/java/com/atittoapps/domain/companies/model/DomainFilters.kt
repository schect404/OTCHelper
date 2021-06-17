package com.atittoapps.domain.companies.model

import java.text.DecimalFormat

data class DomainFilters(
    val shouldShowRestOfPink: Boolean = false,
    val priceRange: DomainRange<Double> = DomainRange(0.0001, 0.01),
    val minVolume: Long = 100000,
    val floatRange: DomainRange<Long> = DomainRange(500 * 1000 * 1000, 1500 * 1000 * 1000),
    val shouldShowWithNoSharesNumberInformation: Boolean = false,
    val shouldPushesSound: Boolean = true
)

data class DomainRange<T>(
    val min: T?,
    val max: T?
) {

    private val format = DecimalFormat("##0.####")

    fun getMinText() = min?.let { format.format(it) }

    fun getMaxText() = max?.let { format.format(it) }
}
