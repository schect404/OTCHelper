package com.example.otchelper.details.reports.sec

import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.example.domain.companies.model.DomainOtcNews
import com.example.domain.companies.model.DomainSecReport
import com.example.domain.companies.model.DomainStock

interface ReportsContract {

    sealed class ViewIntent : BaseViewIntent {
        class Initial(
            val stock: DomainStock,
            val isSec: Boolean
        ) : ViewIntent()
    }

    data class ViewState(
        val news: List<ReportsItems> = listOf()
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        object Skeleton : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(news = ReportsItems.Skeleton.getSkeletons())
        }

        class LoadedReports(val news: List<DomainSecReport>) : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState.copy(
                news = news.map { ReportsItems.Sec(it) }
            )
        }
    }
}