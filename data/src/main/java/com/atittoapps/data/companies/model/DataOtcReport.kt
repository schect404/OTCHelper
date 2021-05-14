package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.DomainSecReport
import com.google.gson.annotations.SerializedName

data class DataOtcReport(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("releaseDate")
    val receivedDate: Long?
) {

    fun toDomain() = DomainSecReport(id, name, receivedDate, true, null)

}