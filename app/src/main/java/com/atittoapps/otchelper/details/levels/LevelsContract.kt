package com.atittoapps.otchelper.details.levels

import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.atittoapps.domain.companies.model.DomainOtcNews
import com.atittoapps.domain.companies.model.DomainStock

interface LevelsContract {

    sealed class ViewIntent : BaseViewIntent {
        class Initial(
            val stock: DomainStock
        ) : ViewIntent()
    }

    data class ViewState(
        val levels: List<LevelsItems> = listOf()
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        object Skeleton : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(levels = LevelsItems.Skeleton.createSkeletons())
        }

        class LoadedNews(val levels: List<String>) : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState.copy(
                levels = levels.map { LevelsItems.Level(it) }
            )
        }
    }
}