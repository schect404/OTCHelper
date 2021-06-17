package com.atittoapps.otchelper.details.otcnews

import com.atittoapps.domain.companies.model.DomainOtcNews

sealed class OtcNewsItems {

    object Skeleton : OtcNewsItems() {
        fun getSkeletons() = arrayListOf<Skeleton>().apply {
            for(i in 1..10) add(Skeleton)
        }
    }

    data class News(
        val news: DomainOtcNews
    ) : OtcNewsItems()
}