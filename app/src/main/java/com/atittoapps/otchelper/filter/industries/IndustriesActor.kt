package com.atittoapps.otchelper.filter.industries

import com.atitto.mviflowarch.base.BaseActor
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class IndustriesActor(private val companiesInteractor: CompaniesInteractor):
    BaseActor<IndustriesContract.ViewIntent, StubModelIntent, IndustriesContract.ViewState, IndustriesContract.PartialChange>() {

    override val initialState = IndustriesContract.ViewState()

    override fun Flow<IndustriesContract.ViewIntent>.handleIntent(): Flow<IndustriesContract.PartialChange> {
        val initialFlow = filterIsInstance<IndustriesContract.ViewIntent.Initial>()
            .map { IndustriesContract.PartialChange.Loaded(companiesInteractor.getAllIndustries(), it.currentChecked) }

        val checkAllFlow = filterIsInstance<IndustriesContract.ViewIntent.CheckAll>()
            .map { IndustriesContract.PartialChange.CheckAll(it.isToSelect) }

        val changeFlow = filterIsInstance<IndustriesContract.ViewIntent.ItemChanged>()
            .map { IndustriesContract.PartialChange.ItemChanged(it.industry) }

        return merge(initialFlow, checkAllFlow, changeFlow)
    }
}