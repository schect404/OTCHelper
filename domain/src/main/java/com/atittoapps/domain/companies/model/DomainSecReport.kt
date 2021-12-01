package com.atittoapps.domain.companies.model

data class DomainSecReport(
    val id: String?,
    val formType: String?,
    val receivedDate: Long?,
    val filedAsHtml: Boolean?,
    val guid: String?
) {

    fun getUrl() = guid?.let {
        "https://www.otcmarkets.com/filing/html?id=%s&guid=%s".format(
            id,
            it.split("-").getOrNull(1)
        )
    }

    fun getUrlOtc() = id?.let {
        "http://docs.google.com/viewer?url=https://otcmarkets.com/otcapi/company/financial-report/%s/content".format(it)
    }

    fun getDate() = receivedDate.getDateString()
}