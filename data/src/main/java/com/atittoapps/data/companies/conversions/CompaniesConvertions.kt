package com.atittoapps.data.companies.conversions

import com.atittoapps.data.companies.model.Stock
import com.atittoapps.data.companies.model.Stocks
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.SharesPage

fun Stocks.toDomain(currentPage: Int = 0) =
    SharesPage(
        pages,
        if (currentPage + 1 >= pages) null else currentPage + 1,
        stocks?.map { it.toDomain() } ?: listOf()
    )

fun Stock.toDomain() =
    DomainStock(
        symbol = symbol,
        securityName = securityName,
        market = market,
        securityType = securityType,
        volume = volume,
        lastSale = price,
        industry = industry
    )