package com.example.data.companies.model

import com.example.domain.companies.model.DomainSecReport
import com.google.gson.annotations.SerializedName

data class DataSecReport(
    @SerializedName("id")
    val id: String?,
    @SerializedName("formType")
    val formType: String?,
    @SerializedName("receivedDate")
    val receivedDate: Long?,
    @SerializedName("filedAsHtml")
    val filedAsHtml: Boolean?,
    @SerializedName("guid")
    val guid: String?
) {

    fun toDomain() = DomainSecReport(id, formType, receivedDate, filedAsHtml, guid)

}