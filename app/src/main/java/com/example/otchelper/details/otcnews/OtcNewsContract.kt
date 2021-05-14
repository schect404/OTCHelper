package com.example.otchelper.details.otcnews

import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.example.domain.companies.model.DomainOtcNews
import com.example.domain.companies.model.DomainStock

interface OtcNewsContract {

    sealed class ViewIntent : BaseViewIntent {
        class Initial(
            val stock: DomainStock,
            val isExternal: Boolean
        ) : ViewIntent()
    }

    data class ViewState(
        val news: List<OtcNewsItems> = listOf()
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        object Skeleton : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(news = OtcNewsItems.Skeleton.getSkeletons())
        }

        class LoadedNews(val news: List<DomainOtcNews>) : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState.copy(
                news = news.map { OtcNewsItems.News(it) }
            )
        }
    }
}