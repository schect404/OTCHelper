package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class CompanyInside(
    @SerializedName("annualHigh")
    val annualHigh: Double?,
    @SerializedName("annualLow")
    val annualLow: Double?,
    @SerializedName("lastSale")
    val lastSale: Double?,
    @SerializedName("previousClose")
    val previousClose: Double?,
    @SerializedName("openingPrice")
    val openingPrice: Double?
)