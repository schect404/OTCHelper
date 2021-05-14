package com.example.data.companies.model

import com.google.gson.annotations.SerializedName

data class Stock(
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("securityName")
    val securityName: String?,
    @SerializedName("market")
    val market: String?,
    @SerializedName("securityType")
    val securityType: String?,
    @SerializedName("volume")
    val volume: Double?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("industry")
    val industry: String?
)