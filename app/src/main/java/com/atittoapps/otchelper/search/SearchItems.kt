package com.atittoapps.otchelper.search

import com.atittoapps.domain.companies.model.DomainSymbols

sealed class SearchItems {

    open fun isPassedToQuery(query: String) = true

    data class Result(val symbol: DomainSymbols) : SearchItems() {
        override fun isPassedToQuery(query: String): Boolean {
            return symbol.ticker.contains(query.toUpperCase()) || symbol.fullName.toLowerCase().contains(query.toLowerCase())
        }
    }

}
