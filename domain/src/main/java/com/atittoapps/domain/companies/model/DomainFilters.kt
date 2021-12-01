package com.atittoapps.domain.companies.model

import java.text.DecimalFormat

data class DomainFilters(
    val shouldShowRestOfPink: Boolean = false,
    val priceRange: DomainRange<Double> = DomainRange(0.0001, 0.01),
    val minVolume: Long = 100000,
    val floatRange: DomainRange<Long> = DomainRange(500 * 1000 * 1000, 1500 * 1000 * 1000),
    val shouldShowWithNoSharesNumberInformation: Boolean = false,
    val shouldPushesSound: Boolean = true,
    val sortingBy: SortingBy = SortingBy.PRICE_ASCENDING,
    val industries: List<Industry> = listOf(),
    val sharesRange: DomainRange<Int> = DomainRange<Int>(0, 100),
    val asRange: DomainRange<Long> = DomainRange(100000, 15000000000),
    val markets: List<Market> = listOf()
)

data class DomainRange<T>(
    val min: T?,
    val max: T?
) {

    private val format = DecimalFormat("##0.####")

    fun getMinText() = min?.let { format.format(it) }

    fun getMaxText() = max?.let { format.format(it) }
}
