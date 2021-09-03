package com.atittoapps.domain.companies.model

import kotlin.math.roundToInt

data class DomainStock(
    val symbol: String?,
    val securityName: String? = null,
    val market: String? = null,
    val securityType: String? = null,
    val volume: Double? = null,
    val price: Double? = null,
    val outstandingShares: Long? = null,
    val outstandingSharesAsOfDate: Long? = null,
    val authorisedShares: Long? = null,
    val authorisedSharesAsOfDate: Long? = null,
    val restrictedShares: Long? = null,
    val restrictedSharesAsOfDate: Long? = null,
    val unrestrictedShares: Long? = null,
    val unrestrictedSharesAsOfDate: Long? = null,
    val publicFloat: Long? = null,
    val publicFloatAsOfDate: Long? = null,
    val dtcShares: Long? = null,
    val dtcSharesAsOfDate: Long? = null,
    val parValue: Double? = null,
    val previousClose: Double? = null,
    val openingPrice: Double? = null,
    val lastSale: Double? = null,
    val annualHigh: Double? = null,
    val annualLow: Double? = null,
    val isPriceGood: Boolean = false,
    val priceFirst: Double? = null,
    val isFavourite: Boolean = false,
    val isNew: Boolean? = false,
    val historicalData: List<HistoricalDataItem> = emptyList(),
    val lastPriceSaw: Double? = null,
    val isPriceChanged: Boolean = false,
    val fromLastMax: Double? = null,
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val website: String? = null,
    val businessDesc: String? = null,
    val email: String? = null,
    val numberOfEmployees: String? = null,
    val industry: String? = null,
    val marketCap: Double? = null,
    val estimatedMarketCapAsOfDate: Long? = null,
    val officers: List<DomainOfficer> = listOf(),
    val alreadyLoaded: Boolean = false,
    val currentPossible: Boolean = false,
    val compliantToShareStructureFilter: Boolean = true,
    val alreadyFiltered: Boolean = false
) {

    fun chartCouldNotLoad() = alreadyLoaded && historicalData.isNullOrEmpty()

    fun getAuthOut(): String {
        val authNotNull = authorisedShares?.toDouble() ?: return "n/a"
        val outNotNull = outstandingShares?.toDouble() ?: return "n/a"
        return "%s%s".format(((outNotNull / authNotNull)*100).roundToInt(), "%")
    }

    fun getAuthOutRaw(): Int? {
        val authNotNull = authorisedShares?.toDouble() ?: return null
        val outNotNull = outstandingShares?.toDouble() ?: return null
        return ((outNotNull / authNotNull)*100).roundToInt()
    }

    fun isFullyAppropriate(shouldSkipSharesInfo: Boolean, floatRange: DomainRange<Long>, sharesRange: DomainRange<Int>, asRange: DomainRange<Long>) =
        isFloatOrDtcAppropriate(shouldSkipSharesInfo, floatRange) && isOutStandingSharesAppropriate(
            shouldSkipSharesInfo
        )&&(isSharesRangeAppropriate(shouldSkipSharesInfo, sharesRange))&&(isASRangeAppropriate(shouldSkipSharesInfo, asRange))

    fun isPriceAppropriate(): Boolean {
        val annualHighNotNull = annualHigh ?: 0.0
        val annualLowNotNull = annualLow ?: 0.0
        val priceNotNull = price ?: 0.0
        val average = (annualHighNotNull + annualLowNotNull) / 2
        return (priceNotNull <= average / 2)
    }

    fun isSharesRangeAppropriate(shouldSkipSharesInfo: Boolean, sharesRange: DomainRange<Int>): Boolean {
        val authOut = getAuthOutRaw() ?: return shouldSkipSharesInfo
        val min = sharesRange.min ?: 0
        val max = sharesRange.max ?: 100
        return (authOut >= min )&&(authOut <= max)
    }

    fun isASRangeAppropriate(shouldSkipSharesInfo: Boolean, asRange: DomainRange<Long>): Boolean {
        val authOut = authorisedShares ?: return shouldSkipSharesInfo
        val min = asRange.min ?: 0
        val max = asRange.max ?: 100
        return (authOut >= min )&&(authOut <= max)
    }

    private fun isFloatOrDtcAppropriate(
        shouldSkipSharesInfo: Boolean,
        floatRange: DomainRange<Long>
    ): Boolean {
        val publicFloatNotNull = publicFloat ?: 0
        val dtcSharesNotNull = dtcShares ?: 0
        if (publicFloatNotNull == 0L && dtcSharesNotNull == 0L) return shouldSkipSharesInfo
        val valueToCompare =
            if (publicFloatNotNull >= dtcSharesNotNull) publicFloatNotNull else dtcSharesNotNull
        return valueToCompare in (floatRange.min ?: 0)..(floatRange.max ?: Long.MAX_VALUE)
    }

    private fun isOutStandingSharesAppropriate(shouldSkipSharesInfo: Boolean): Boolean {
        return true
        val outstandingSharesNotNull = outstandingShares ?: return shouldSkipSharesInfo
        val authorisedSharesNotNull = authorisedShares ?: return shouldSkipSharesInfo
        return outstandingSharesNotNull / authorisedSharesNotNull < 0.9
    }

    fun makeWatchlistTitleString() = "<b>%s</b> | %s | %s".format(symbol, market, securityType)

}