package com.example.data.companies.model

import com.google.gson.annotations.SerializedName

data class DataOtcNews(
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("createdDate")
    val createdDate: Long?,
    @SerializedName("newsTypeDescript")
    val newsTypeDescript: String?
)
