package com.atittoapps.data.companies.model

import com.google.gson.annotations.SerializedName

data class ApiLevels(
        @SerializedName("levels")
        val levels: List<Double>?
)
