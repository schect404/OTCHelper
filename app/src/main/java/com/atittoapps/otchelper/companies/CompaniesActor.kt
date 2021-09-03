package com.atittoapps.otchelper.companies

import android.util.Log
import com.atitto.mviflowarch.base.BaseActor
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.CompaniesInteractor
import com.atittoapps.domain.companies.model.DomainStock
import kotlinx.coroutines.flow.*

class CompaniesActor(
    private val interactor: CompaniesInteractor,
    private val sharedPrefsProvider: SharedPrefsProvider
) : BaseActor<CompaniesContract.ViewIntent, CompaniesContract.ModelIntent, CompaniesContract.ViewState, CompaniesContract.PartialChange>() {

    override val initialState = CompaniesContract.ViewState()

    override fun Flow<CompaniesContract.ViewIntent>.handleIntent(): Flow<CompaniesContract.PartialChange> {

        val initialFlow = filterIsInstance<CompaniesContract.ViewIntent.LoadPage>()
            .take(1)
            .flatMapConcat {
                val page = viewState?.value?.nextPage
                if (page == null) flowOf(CompaniesContract.PartialChange.RemoveSkeleton)
                else if (viewState?.value?.items?.filter { it is CompaniesItems.Skeleton }
                        ?.isEmpty() == false) flowOf(CompaniesContract.PartialChange.Stub)
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

        val primaryFilter = filterIsInstance<CompaniesContract.ViewIntent.InitialFilter>()
                .take(1)
                .onEach { Log.e("TAG_OTC", "CompaniesContract.ViewIntent.InitialFilter") }
            .flatMapConcat { interactor.getPrimaryFiltered().runWithoutProgress() }
            .map<List<DomainStock>, CompaniesContract.PartialChange> { CompaniesContract.PartialChange.PrimaryLoaded(it) }
            .onStart { emit(CompaniesContract.PartialChange.LoadedSkeleton) }

        val loadByItemFlow = filterIsInstance<CompaniesContract.ViewIntent.LoadByFiltered>()
            .map { it.list.filterNot { it.alreadyLoaded } }
            .flatMapConcat { stocks ->
                flow {
                    stocks.forEach {
                        val fullProfile = interactor.getFullCompany(it.copy(alreadyFiltered = true)).runWithoutProgress().first()
                        if(!fullProfile.compliantToShareStructureFilter) emit(CompaniesContract.PartialChange.ItemRemoved(fullProfile.copy(alreadyLoaded = true)))
                        else {
                            val stock = interactor.getHistoricalData(fullProfile).zip(interactor.getIsCurrentPossibleBasedOnReports(fullProfile)) { stock1, stock2 ->
                                stock2.copy(historicalData = stock1.historicalData,
                                        previousClose = stock1.historicalData.lastOrNull()?.close,
                                        openingPrice = stock1.historicalData.lastOrNull()?.open)
                            }.first()
                            emit(CompaniesContract.PartialChange.ItemLoaded(stock))
                        }
                    }
                }.runWithoutProgress()
            }

        val updateFlow = filterIsInstance<CompaniesContract.ViewIntent.UpdateCache>()
                .onEach { Log.e("TAG_OTC", "CompaniesContract.ViewIntent.UpdateCache") }
                .flatMapConcat {
                    interactor.updateCache(it.stocks)
                }.map { CompaniesContract.PartialChange.Stub }

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
            removeFromWatchlistFlow,
            primaryFilter,
            loadByItemFlow,
                updateFlow
        )
    }

    override fun CompaniesContract.PartialChange.getSingleEvent() = when (this) {
        is CompaniesContract.PartialChange.RemovedFromWatchlist -> CompaniesContract.ModelIntent.RemoveFromWatchlist
        is CompaniesContract.PartialChange.AddedToWatchlist -> CompaniesContract.ModelIntent.AddedToWatchlist
        else -> null
    }
}