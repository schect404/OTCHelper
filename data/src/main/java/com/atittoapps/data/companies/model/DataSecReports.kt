package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class DataSecReports(
    @SerializedName("records")
    val records: List<DataSecReport>?
)