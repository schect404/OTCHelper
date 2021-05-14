package com.example.data.companies.model

import com.google.gson.annotations.SerializedName

data class Stocks(
    @SerializedName("pages")
    val pages: Int = 0,
    @SerializedName("stocks")
    val stocks: List<Stock>?
)