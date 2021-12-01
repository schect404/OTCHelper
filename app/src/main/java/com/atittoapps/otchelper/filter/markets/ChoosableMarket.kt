package com.atittoapps.otchelper.filter.markets

import com.atittoapps.domain.companies.model.Market

data class ChoosableMarket(
    val market: Market,
    val isChecked: Boolean
)

fun List<ChoosableMarket>.allChecked() = firstOrNull { !it.isChecked } == null
