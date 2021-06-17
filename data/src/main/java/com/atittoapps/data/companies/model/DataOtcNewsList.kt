package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.DomainOtcNews
import com.google.gson.annotations.SerializedName

data class DataOtcNewsList(
    @SerializedName("records")
    val records: List<DataOtcNews>?
) {

    fun toDomain() = records?.map {
        DomainOtcNews(
            id = it.id,
            title = it.title,
            createdDate = it.createdDate,
            newsTypeDescript = it.newsTypeDescript
        )
    }

}
