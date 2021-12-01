package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.DomainFilters
import com.atittoapps.domain.companies.model.DomainRange
import com.atittoapps.domain.companies.model.SortingBy
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
        val shouldPushesSound: Boolean = true,
        @SerializedName("sortingBy")
        val sortingBy: DataSortingBy = DataSortingBy.PRICE_ASCENDING,
        @SerializedName("industries")
        val industries: List<DataIndustry> = listOf(),
        @SerializedName("sharesStructurePercent")
        val sharesRange: DataRange<Int> = DataRange(0, 100),
        @SerializedName("as")
        val authorisedShares: DataRange<Long> = DataRange(100000, 15000000000),
        @SerializedName("markets")
        val markets: List<DataMarket> = listOf()
) {

    fun getMarketsString(allMarkets: List<DataMarket>) = StringBuilder().apply {
        markets.forEach {
            append(it.id)
            append(",")
        }
        if (!this.isEmpty()) {
            removeSuffix(",")
        } else {
            allMarkets.forEach {
                append(it.id)
                append(",")
            }
            if (!isEmpty()) {
                removeSuffix(",")
            }
        }
    }.toString()

    fun getIndustriesString() = StringBuilder().apply {
        industries.forEach {
            append(it.id)
            append(",")
        }
        if (!this.isEmpty()) {
            removeSuffix(",")
        }
    }

    fun toDomainFilters() = DomainFilters(
            shouldShowRestOfPink,
            priceRange.toDomainRange(),
            minVolume,
            floatRange.toDomainRange(),
            shouldShowWithNoSharesNumberInformation,
            shouldPushesSound,
            sortingBy.toDomain(),
            industries.map { it.toDomain() },
            sharesRange.toDomainRange(),
            authorisedShares.toDomainRange(),
            markets.map { it.toDomain() }
    )

}

fun SortingBy.toData() = when (this) {
    SortingBy.PRICE_ASCENDING -> DataSortingBy.PRICE_ASCENDING
    SortingBy.PRICE_DESCENDING -> DataSortingBy.PRICE_DESCENDING
    SortingBy.MARKET_ASCENDING -> DataSortingBy.MARKET_ASCENDING
    SortingBy.MARKET_DESCENDING -> DataSortingBy.MARKET_DESCENDING
    SortingBy.VOLUME_ASCENDING -> DataSortingBy.VOLUME_ASCENDING
    SortingBy.VOLUME_DESCENDING -> DataSortingBy.VOLUME_DESCENDING
}

enum class DataSortingBy {
    @SerializedName("priceAscending")
    PRICE_ASCENDING,

    @SerializedName("priceDescending")
    PRICE_DESCENDING,

    @SerializedName("marketAscending")
    MARKET_ASCENDING,

    @SerializedName("marketDescending")
    MARKET_DESCENDING,

    @SerializedName("volumeAscending")
    VOLUME_ASCENDING,

    @SerializedName("volumeDescending")
    VOLUME_DESCENDING;

    fun toDomain() = when (this) {
        PRICE_ASCENDING -> SortingBy.PRICE_ASCENDING
        PRICE_DESCENDING -> SortingBy.PRICE_DESCENDING
        MARKET_ASCENDING -> SortingBy.MARKET_ASCENDING
        MARKET_DESCENDING -> SortingBy.MARKET_DESCENDING
        VOLUME_ASCENDING -> SortingBy.VOLUME_ASCENDING
        VOLUME_DESCENDING -> SortingBy.VOLUME_DESCENDING
    }
}

fun DomainFilters.toData() =
    DataFilters(
            shouldShowRestOfPink,
            priceRange.toDataRange(),
            minVolume,
            floatRange.toDataRange(),
            shouldShowWithNoSharesNumberInformation,
            shouldPushesSound,
            sortingBy.toData(),
            industries.map { it.toData() },
            sharesRange.toDataRange(),
            asRange.toDataRange(),
            markets.map { it.toData() }
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