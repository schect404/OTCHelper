package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class DataExternalNews(
    @SerializedName("id")
    val id: String?,
    @SerializedName("headline")
    val headline: String?,
    @SerializedName("pubDate")
    val pubDate: Long?,
    @SerializedName("sourceName")
    val sourceName: String?
)
