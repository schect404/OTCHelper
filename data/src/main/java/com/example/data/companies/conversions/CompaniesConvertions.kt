package com.example.data.companies.conversions

import com.example.data.companies.model.Stock
import com.example.data.companies.model.Stocks
import com.example.domain.companies.model.DomainStock
import com.example.domain.companies.model.SharesPage

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
        price = price,
        industry = industry
    )