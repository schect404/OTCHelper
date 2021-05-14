package com.atittoapps.domain.companies.model;

import java.text.SimpleDateFormat
import java.util.Date

data class DomainOtcNews(
        val id: String?,
        val title: String?,
        val createdDate: Long?,
        val newsTypeDescript: String?
) {
    fun getNewsUrl(symbol: String) = title?.let {
        "https://www.otcmarkets.com/stock/%s/news/%s?id=%s".format(
                symbol,
                it.replace(" ", "-"),
                id
        )
    }

    fun getExternalNewsUrl(symbol: String) = id?.let {
        "https://www.otcmarkets.com/stock/%s/news/story?e&id=%s".format(symbol, it)
    }

    fun getDate() = createdDate.getDateString()
}

val formatter = SimpleDateFormat("dd.MM.YYY")

fun Long?.getDateString() = this?.let { formatter.format(Date(it)) }
