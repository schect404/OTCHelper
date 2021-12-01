package com.atittoapps.otchelper.filter.markets

import com.atitto.mviflowarch.base.BaseActor
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class MarketsActor(private val companiesInteractor: CompaniesInteractor):
    BaseActor<MarketsContract.ViewIntent, StubModelIntent, MarketsContract.ViewState, MarketsContract.PartialChange>() {

    override val initialState = MarketsContract.ViewState()

    override fun Flow<MarketsContract.ViewIntent>.handleIntent(): Flow<MarketsContract.PartialChange> {
        val initialFlow = filterIsInstance<MarketsContract.ViewIntent.Initial>()
            .map { MarketsContract.PartialChange.Loaded(companiesInteractor.getAllMarkets(), it.currentChecked) }

        val checkAllFlow = filterIsInstance<MarketsContract.ViewIntent.CheckAll>()
            .map { MarketsContract.PartialChange.CheckAll(it.isToSelect) }

        val changeFlow = filterIsInstance<MarketsContract.ViewIntent.ItemChanged>()
            .map { MarketsContract.PartialChange.ItemChanged(it.market) }

        return merge(initialFlow, checkAllFlow, changeFlow)
    }
}