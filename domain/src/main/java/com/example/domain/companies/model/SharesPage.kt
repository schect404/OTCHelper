package com.example.domain.companies.model

data class SharesPage(
    val pages: Int,
    val page: Int?,
    val shares: List<DomainStock>
)