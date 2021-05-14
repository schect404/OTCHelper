package com.example.otchelper.companies

import com.example.domain.companies.model.DomainStock

sealed class CompaniesItems {

    data class Stock(val domainStock: DomainStock) : CompaniesItems()

    object Skeleton : CompaniesItems() {
        fun createSkeletons() = arrayListOf<Skeleton>().apply {
            for (i in 0..5) add(Skeleton)
        }
    }
}
