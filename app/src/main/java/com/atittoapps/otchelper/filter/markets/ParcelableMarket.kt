package com.atittoapps.otchelper.filter.markets

import android.os.Parcelable
import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableMarket (
    val id: String,
    val text: String
): Parcelable {
    fun toDomain() = Market(id, text)
}

fun Market.toParcelable() = ParcelableMarket(id, text)