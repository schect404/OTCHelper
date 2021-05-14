package com.example.otchelper.watchlist

import com.atitto.mviflowarch.base.BaseModelIntent
import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.example.domain.companies.model.DomainStock

interface WatchlistContract {

    sealed class ViewIntent : BaseViewIntent {
        object Initial : ViewIntent()
        class RemoveFromWatchlist(val stock: DomainStock) : ViewIntent()
    }

    sealed class ModelIntent : BaseModelIntent {
        object RemovedFromWatchlist : ModelIntent()
    }

    data class ViewState(
        val items: List<DomainStock> = listOf()
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        class Loaded(private val list: List<DomainStock>) : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(items = list)
        }

        class Removed(private val stock: DomainStock) : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(items = initialState.items.filterNot { it.symbol == stock.symbol })
        }
    }

}