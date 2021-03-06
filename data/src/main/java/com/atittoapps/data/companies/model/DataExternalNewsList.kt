package com.atittoapps.data.companies.model

import com.atittoapps.domain.companies.model.DomainOtcNews
import com.google.gson.annotations.SerializedName

data class DataExternalNewsList(
    @SerializedName("records")
    val records: List<DataExternalNews>?
) {

    fun toDomain() = records?.map {
        DomainOtcNews(
            id = it.id,
            title = it.headline,
            createdDate = it.pubDate,
            newsTypeDescript = it.sourceName
        )
    }

}
