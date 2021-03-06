package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.DomainSymbols
import com.google.gson.annotations.SerializedName

data class DataSymbols (
    @SerializedName("s")
    val ticker: String?,
    @SerializedName("c")
    val fullName: String?
) {
    fun toDomain() = DomainSymbols(ticker ?: "", fullName ?: "")
}