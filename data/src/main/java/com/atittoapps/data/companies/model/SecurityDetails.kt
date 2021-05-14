package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class SecurityDetails(
    @SerializedName("outstandingShares")
    val outstandingShares: Long?,
    @SerializedName("outstandingSharesAsOfDate")
    val outstandingSharesAsOfDate: Long?,
    @SerializedName("authorizedShares")
    val authorisedShares: Long?,
    @SerializedName("authorizedSharesAsOfDate")
    val authorisedSharesAsOfDate: Long?,
    @SerializedName("restrictedShares")
    val restrictedShares: Long?,
    @SerializedName("restrictedSharesAsOfDate")
    val restrictedSharesAsOfDate: Long?,
    @SerializedName("unrestrictedShares")
    val unrestrictedShares: Long?,
    @SerializedName("unrestrictedSharesAsOfDate")
    val unrestrictedSharesAsOfDate: Long?,
    @SerializedName("publicFloat")
    val publicFloat: Long?,
    @SerializedName("publicFloatAsOfDate")
    val publicFloatAsOfDate: Long?,
    @SerializedName("dtcShares")
    val dtcShares: Long?,
    @SerializedName("dtcSharesAsOfDate")
    val dtcSharesAsOfDate: Long?,
    @SerializedName("parValue")
    val parValue: Double?,
    @SerializedName("tierName")
    val tierName: String?,
    @SerializedName("typeName")
    val typeName: String?
)