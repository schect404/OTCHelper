package com.atittoapps.otchelper.details.reports.sec

import com.atitto.mviflowarch.base.BaseActor
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ReportsActor(private val interactor: CompaniesInteractor) : BaseActor<ReportsContract.ViewIntent, StubModelIntent, ReportsContract.ViewState, ReportsContract.PartialChange>() {

    override val initialState = ReportsContract.ViewState()

    override fun Flow<ReportsContract.ViewIntent>.handleIntent(): Flow<ReportsContract.PartialChange> {
        val flow = filterIsInstance<ReportsContract.ViewIntent.Initial>()
            .flatMapConcat {
                flow {
                    if(it.isSec) {
                        interactor.getSecReports(it.stock).runWithoutProgress()
                            .onEach { emit(ReportsContract.PartialChange.LoadedReports(it)) }
                            .onStart { emit(ReportsContract.PartialChange.Skeleton) }
                            .collect()
                    } else {
                        interactor.getOtcReports(it.stock).runWithoutProgress()
                            .onEach { emit(ReportsContract.PartialChange.LoadedReports(it)) }
                            .onStart { emit(ReportsContract.PartialChange.Skeleton) }
                            .collect()
                    }
                }
            }

        return merge(flow)
    }

}