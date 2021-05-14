package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class DataOtcReports(
    @SerializedName("records")
    val records: List<DataOtcReport>?
)