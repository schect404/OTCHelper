package com.atittoapps.otchelper.search

import com.atitto.mviflowarch.base.BaseActor
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class SearchActor(private val interactor: CompaniesInteractor) : BaseActor<SearchContract.ViewIntent, SearchContract.ModelIntent, SearchContract.ViewState, SearchContract.PartialChange>() {

    override val initialState = SearchContract.ViewState()

    override fun Flow<SearchContract.ViewIntent>.handleIntent(): Flow<SearchContract.PartialChange> {

        val initialFlow = filterIsInstance<SearchContract.ViewIntent.Initial>()
            .flatMapConcat { interactor.getSymbols().runWithProgress() }
            .map { SearchContract.PartialChange.Loaded(it) }

        val searchFlow = filterIsInstance<SearchContract.ViewIntent.Search>()
            .map { SearchContract.PartialChange.Filter(it.query) }

        val goToDetailsFlow = filterIsInstance<SearchContract.ViewIntent.GoToDetails>()
            .flatMapConcat { interactor.getCompanyProfile(it.symbols).runWithProgress() }
            .map { SearchContract.PartialChange.GoToDetails(it) }

        return merge(initialFlow, searchFlow, goToDetailsFlow)
    }

    override fun SearchContract.PartialChange.getSingleEvent() = when (this) {
        is SearchContract.PartialChange.GoToDetails -> SearchContract.ModelIntent.GoToDetails(this.stock)
        else -> null
    }

}