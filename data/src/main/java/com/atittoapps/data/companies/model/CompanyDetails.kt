package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class CompanyDetails(
    @SerializedName("securities")
    val securities: List<SecurityDetails>?,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("website")
    val website: String? = null,
    @SerializedName("businessDesc")
    val businessDesc: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("numberOfEmployees")
    val numberOfEmployees: String? = null,
    @SerializedName("estimatedMarketCap")
    val estimatedMarketCap: Double? = null,
    @SerializedName("estimatedMarketCapAsOfDate")
    val estimatedMarketCapAsOfDate: Long? = null
)