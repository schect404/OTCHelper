package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class DataOfficer(
    @SerializedName("name")
    val name: String?,
    @SerializedName("title")
    val title: String?
)
