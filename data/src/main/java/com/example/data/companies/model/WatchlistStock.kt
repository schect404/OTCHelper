package com.example.data.companies.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.companies.model.DomainStock

@Entity(tableName = "watchlist", indices = [Index(value = ["symbol"], unique = true)])
data class WatchlistStock(
    @PrimaryKey
    val symbol: String,
    val market: String?,
    val securityType: String?,
    val price: Double?,
    val lastPriceSaw: Double?,
    val name: String?,
    val city: String?,
    val country: String?,
    val website: String?,
    val businessDesc: String?,
    val email: String?,
    val numberOfEmployees: String?,
    val industry: String?
) {

    fun toDomain() = DomainStock(
        symbol = symbol,
        market = market,
        securityName = null,
        securityType = securityType,
        priceFirst = price,
        price = null,
        volume = null,
        isFavourite = true,
        lastPriceSaw = lastPriceSaw,
        name = name,
        city = city,
        country = country,
        website = website,
        businessDesc = businessDesc,
        email = email,
        numberOfEmployees = numberOfEmployees,
        industry = industry
    )
}

fun DomainStock.toWatchlist() = WatchlistStock(
    symbol ?: "",
    market,
    securityType,
    lastSale,
    lastPriceSaw,
    name,
    city,
    country,
    website,
    businessDesc,
    email,
    numberOfEmployees,
    industry
)