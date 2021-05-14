package com.example.otchelper.details.reports.sec

import com.example.domain.companies.model.DomainSecReport

sealed class ReportsItems {

    object Skeleton : ReportsItems() {
        fun getSkeletons() = arrayListOf<Skeleton>().apply {
            for (i in 1..10) add(Skeleton)
        }
    }

    data class Sec(
        val sec: DomainSecReport
    ) : ReportsItems()
}