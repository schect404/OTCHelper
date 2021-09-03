package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.Industry
import com.google.gson.annotations.SerializedName

data class DataIndustry (
    @SerializedName("sic")
    val id: String,
    @SerializedName("name")
    val text: String
) {
    fun toDomain() = Industry(id, text)
}

fun Industry.toData() = DataIndustry(id, text)