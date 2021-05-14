package com.example.data.companies.model

import com.example.domain.companies.model.DomainFilters
import com.example.domain.companies.model.DomainRange
import com.google.gson.annotations.SerializedName

data class DataFilters(
    @SerializedName("shouldShowRestOfPink")
    val shouldShowRestOfPink: Boolean = false,
    @SerializedName("priceRange")
    val priceRange: DataRange<Double> = DataRange<Double>(0.0001, 0.005),
    @SerializedName("minVolume")
    val minVolume: Long = 100000,
    @SerializedName("floatRange")
    val floatRange: DataRange<Long> = DataRange(500 * 1000 * 1000, 1500 * 1000 * 1000),
    @SerializedName("shouldShowWithNoSharesNumberInformation")
    val shouldShowWithNoSharesNumberInformation: Boolean = false,
    @SerializedName("shouldPushesSound")
    val shouldPushesSound: Boolean = true
) {

    fun getMarketsString() = if (shouldShowRestOfPink) "6,5,2,1,10,20,21,22" else "6,5,2,1,10,20"

    fun toDomainFilters() = DomainFilters(
        shouldShowRestOfPink,
        priceRange.toDomainRange(),
        minVolume,
        floatRange.toDomainRange(),
        shouldShowWithNoSharesNumberInformation,
        shouldPushesSound
    )

}

fun DomainFilters.toData() =
    DataFilters(
        shouldShowRestOfPink,
        priceRange.toDataRange(),
        minVolume,
        floatRange.toDataRange(),
        shouldShowWithNoSharesNumberInformation,
        shouldPushesSound
    )

data class DataRange<T>(
    @SerializedName("min")
    val min: T?,
    @SerializedName("max")
    val max: T?
) {

    fun toDomainRange() = DomainRange<T>(min, max)

}

fun <T> DomainRange<T>.toDataRange() =
    DataRange(min, max)