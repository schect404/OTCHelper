package com.example.data.network

object NetworkContract {

    const val FULL_COMPANY_PROFILE = "https://backend.otcmarkets.com/otcapi/company/profile/full/{symbol}"

    const val COMPANIES_FILTER = "https://www.otcmarkets.com/research/stock-screener/api?securityType=ADRs,Common%20Stock,Foreign%20Ordinary%20Shares&pageSize=5"

    const val COMPANY_INSIDE = "https://backend.otcmarkets.com/otcapi/stock/trade/inside/{symbol}"

    const val GET_HISTORICAL_DATA = "https://finnhub.io/api/v1/stock/candle?&resolution=D"

    const val GET_OTC_NEWS = "https://backend.otcmarkets.com/otcapi/company/{symbol}/dns/news?page=1&pageSize=30&sortOn=releaseDate&sortDir=DESC"

    const val GET_NEWS_EXTERNAL = "https://backend.otcmarkets.com/otcapi/company/{symbol}/external/news?page=1&pageSize=30&sortOn=1&sortDir=D"

    const val GET_SEC_REPORT = "https://backend.otcmarkets.com/otcapi/company/sec-filings/{symbol}?page=1&pageSize=50"

    const val SYMBOLS = "https://www.otcmarkets.com/data/symbols"

    const val GET_REPORTS = "https://backend.otcmarkets.com/otcapi/company/{symbol}/financial-report?page=1&pageSize=50&statusId=A&sortOn=releaseDate&sortDir=DESC"

    val tokens = listOf(
        "c1nh0oi37fktf2t2fkbg",
        "c2d5hdiad3i9v1gkcfe0",
        "c2d5rriad3i9v1gkclo0",
        "c2d5s8aad3i9v1gkclvg",
        "c2d5sfaad3i9v1gkcmeg"
    )

    fun getRandomToken() = tokens.random()
}