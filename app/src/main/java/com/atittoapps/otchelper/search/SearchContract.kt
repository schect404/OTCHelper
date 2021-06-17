package com.atittoapps.otchelper.search

import com.atitto.mviflowarch.base.BaseModelIntent
import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.DomainSymbols

interface SearchContract {

    sealed class ViewIntent : BaseViewIntent {

        object Initial : ViewIntent()

        class Search(val query: String) : ViewIntent()

        class GoToDetails(val symbols: DomainSymbols) : ViewIntent()
    }

    sealed class ModelIntent : BaseModelIntent {

        class GoToDetails(val stock: DomainStock) : ModelIntent()
    }

    data class ViewState(
        val initialList: List<SearchItems> = listOf(),
        val currentList: List<SearchItems> = listOf()
    ): BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        class Loaded(private val list: List<DomainSymbols>) : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(initialList = list.map { SearchItems.Result(it) })
        }

        class GoToDetails(val stock: DomainStock) : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState
        }

        class Filter(val query: String) : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(currentList = initialState.initialList.filter {
                    it.isPassedToQuery(query)
                })
        }
    }

}