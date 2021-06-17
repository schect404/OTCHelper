package com.atittoapps.otchelper.details.otcnews

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

class OtcNewsActor(private val interactor: CompaniesInteractor) : BaseActor<OtcNewsContract.ViewIntent, StubModelIntent, OtcNewsContract.ViewState, OtcNewsContract.PartialChange>() {

    override val initialState = OtcNewsContract.ViewState()

    override fun Flow<OtcNewsContract.ViewIntent>.handleIntent(): Flow<OtcNewsContract.PartialChange> {
        val flow = filterIsInstance<OtcNewsContract.ViewIntent.Initial>()
            .flatMapConcat {
                flow {
                    if(it.isExternal) {
                        interactor.getExternalCompanyNews(it.stock).runWithoutProgress()
                            .onEach { emit(OtcNewsContract.PartialChange.LoadedNews(it)) }
                            .onStart { emit(OtcNewsContract.PartialChange.Skeleton) }
                            .collect()
                    } else {
                        interactor.getOtcCompanyNews(it.stock).runWithoutProgress()
                            .onEach { emit(OtcNewsContract.PartialChange.LoadedNews(it)) }
                            .onStart { emit(OtcNewsContract.PartialChange.Skeleton) }
                            .collect()
                    }
                }
            }

        return merge(flow)
    }

}