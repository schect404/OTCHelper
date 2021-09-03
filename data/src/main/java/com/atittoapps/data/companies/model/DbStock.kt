package com.atittoapps.data.companies.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.HistoricalDataItemWrapper
import com.google.gson.Gson

@Entity(tableName = "filtered", indices = [Index(value = ["symbol"], unique = true)])
data class DbStock(
    @PrimaryKey
    val symbol: String,
    val securityName: String?,
    val market: String?,
    val securityType: String?,
    val price: Double?,
    val previousClose: Double?,
    val openingPrice: Double?,
    val lastSale: Double?,
    val isPriceGood: Boolean,
    val historicalData: String,
    val isNew: Boolean,
    val lastPriceSaw: Double?,
    val outstandingShares: Long?,
    val outstandingSharesAsOfDate: Long?,
    val authorisedShares: Long?,
    val authorisedSharesAsOfDate: Long?,
    val restrictedShares: Long?,
    val restrictedSharesAsOfDate: Long?,
    val unrestrictedShares: Long?,
    val unrestrictedSharesAsOfDate: Long?,
    val publicFloat: Long?,
    val publicFloatAsOfDate: Long?,
    val dtcShares: Long?,
    val dtcSharesAsOfDate: Long?,
    val parValue: Double?,
    val estimatedMarketCapAsOfDate: Long?,
    val alreadyLoaded: Boolean?,
    val alreadyFiltered: Boolean?
) {

    fun toDomain(gson: Gson) = DomainStock(
        symbol = symbol,
        market = market,
        securityName = securityName,
        securityType = securityType,
        price = price,
        lastSale = lastSale,
        historicalData = listOf(),
        isPriceGood = isPriceGood,
        openingPrice = openingPrice,
        previousClose = previousClose,
        isNew = isNew,
        lastPriceSaw = lastPriceSaw,
        authorisedShares = authorisedShares,
        authorisedSharesAsOfDate = authorisedSharesAsOfDate,
        outstandingShares = outstandingShares,
        outstandingSharesAsOfDate = outstandingSharesAsOfDate,
        publicFloat = publicFloat,
        publicFloatAsOfDate = publicFloatAsOfDate,
        dtcShares = dtcShares,
        dtcSharesAsOfDate = dtcSharesAsOfDate,
        restrictedShares = restrictedShares,
        restrictedSharesAsOfDate = restrictedSharesAsOfDate,
        unrestrictedShares = unrestrictedShares,
        unrestrictedSharesAsOfDate = unrestrictedSharesAsOfDate,
        parValue = parValue,
        estimatedMarketCapAsOfDate = estimatedMarketCapAsOfDate,
            alreadyLoaded = false,
            alreadyFiltered = alreadyFiltered ?: false
    )
}

fun DomainStock.toDbStock(gson: Gson) = DbStock(
    symbol ?: "",
    securityName,
    market,
    securityType,
    price,
    previousClose,
    openingPrice,
    lastSale,
    isPriceGood,
    gson.toJson(HistoricalDataItemWrapper(historicalData)),
    isNew ?: false,
    lastPriceSaw = lastPriceSaw,
    authorisedShares = authorisedShares,
    authorisedSharesAsOfDate = authorisedSharesAsOfDate,
    outstandingShares = outstandingShares,
    outstandingSharesAsOfDate = outstandingSharesAsOfDate,
    publicFloat = publicFloat,
    publicFloatAsOfDate = publicFloatAsOfDate,
    dtcShares = dtcShares,
    dtcSharesAsOfDate = dtcSharesAsOfDate,
    restrictedShares = restrictedShares,
    restrictedSharesAsOfDate = restrictedSharesAsOfDate,
    unrestrictedShares = unrestrictedShares,
    unrestrictedSharesAsOfDate = unrestrictedSharesAsOfDate,
    parValue = parValue,
    estimatedMarketCapAsOfDate = estimatedMarketCapAsOfDate,
        alreadyLoaded = alreadyLoaded,
        alreadyFiltered = alreadyFiltered
)