package com.atittoapps.otchelper.companies

import com.atitto.mviflowarch.base.BaseActor
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class CompaniesActor(
    private val interactor: CompaniesInteractor,
    private val sharedPrefsProvider: SharedPrefsProvider
) : BaseActor<CompaniesContract.ViewIntent, CompaniesContract.ModelIntent, CompaniesContract.ViewState, CompaniesContract.PartialChange>() {

    override val initialState = CompaniesContract.ViewState()

    override fun Flow<CompaniesContract.ViewIntent>.handleIntent(): Flow<CompaniesContract.PartialChange> {

        val initialFlow = filterIsInstance<CompaniesContract.ViewIntent.LoadPage>()
            .flatMapConcat {
                val page = viewState?.value?.nextPage
                if (page == null) flowOf(CompaniesContract.PartialChange.RemoveSkeleton)
                else if(viewState?.value?.items?.filter { it is CompaniesItems.Skeleton }?.isEmpty() == false) flowOf(CompaniesContract.PartialChange.Stub)
                else
                    flow<CompaniesContract.PartialChange> {

                        interactor.fetchPage(page).runWithoutProgress()
                            .onEach {
                                emit(
                                    CompaniesContract.PartialChange.Loaded(
                                        it.shares,
                                        it.page
                                    )
                                )
                            }
                            .collect()
                    }.onStart {
                        emit(CompaniesContract.PartialChange.LoadedSkeleton)
                    }
            }

        val addToWatchlistFlow = filterIsInstance<CompaniesContract.ViewIntent.AddToWatchList>()
            .flatMapConcat { interactor.addWatchlist(it.stock).runWithProgress() }
            .map { CompaniesContract.PartialChange.AddedToWatchlist(it) }

        val removeFromWatchlistFlow =
            filterIsInstance<CompaniesContract.ViewIntent.RemoveFromWatchList>()
                .flatMapConcat { interactor.removeFromWatchlist(it.stock).runWithProgress() }
                .map { CompaniesContract.PartialChange.RemovedFromWatchlist(it) }

        return merge(
            initialFlow,
            addToWatchlistFlow,
            removeFromWatchlistFlow
        )
    }

    override fun CompaniesContract.PartialChange.getSingleEvent() = when (this) {
        is CompaniesContract.PartialChange.RemovedFromWatchlist -> CompaniesContract.ModelIntent.RemoveFromWatchlist
        is CompaniesContract.PartialChange.AddedToWatchlist -> CompaniesContract.ModelIntent.AddedToWatchlist
        else -> null
    }
}