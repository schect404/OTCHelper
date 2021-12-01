package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.Market
import com.google.gson.annotations.SerializedName

data class DataMarket(
        @SerializedName("id")
        val id: String,
        @SerializedName("text")
        val text: String
) {
    fun toDomain() = Market(id, text)
}

fun Market.toData() = DataMarket(id, text)