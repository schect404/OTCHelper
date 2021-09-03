package com.atittoapps.otchelper.details.levels

import com.atitto.mviflowarch.base.BaseActor
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.*

class LevelsActor(private val interactor: CompaniesInteractor) : BaseActor<LevelsContract.ViewIntent, StubModelIntent, LevelsContract.ViewState, LevelsContract.PartialChange>() {

    override val initialState = LevelsContract.ViewState()

    override fun Flow<LevelsContract.ViewIntent>.handleIntent(): Flow<LevelsContract.PartialChange> {
        val flow = filterIsInstance<LevelsContract.ViewIntent.Initial>()
                .flatMapConcat {
                    flow {
                        interactor.getLevels(it.stock).runWithoutProgress()
                                .onEach { emit(LevelsContract.PartialChange.LoadedNews(it)) }
                                .onStart { emit(LevelsContract.PartialChange.Skeleton) }
                                .collect()

                    }
                }

        return merge(flow)
    }
}