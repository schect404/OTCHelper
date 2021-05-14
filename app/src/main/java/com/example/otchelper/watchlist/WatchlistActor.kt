package com.example.otchelper.watchlist

import com.atitto.mviflowarch.base.BaseActor
import com.example.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class WatchlistActor(private val interactor: CompaniesInteractor) :
    BaseActor<WatchlistContract.ViewIntent, WatchlistContract.ModelIntent, WatchlistContract.ViewState, WatchlistContract.PartialChange>() {

    override val initialState = WatchlistContract.ViewState()

    override fun Flow<WatchlistContract.ViewIntent>.handleIntent(): Flow<WatchlistContract.PartialChange> {

        val initialFlow = filterIsInstance<WatchlistContract.ViewIntent.Initial>()
            .flatMapConcat { interactor.getWatchlist(true).runWithProgress() }
            .map { WatchlistContract.PartialChange.Loaded(it) }

        val removeFromWatchlistFlow =
            filterIsInstance<WatchlistContract.ViewIntent.RemoveFromWatchlist>()
                .flatMapConcat { interactor.removeFromWatchlist(it.stock).runWithoutProgress() }
                .map { WatchlistContract.PartialChange.Removed(it) }

        return merge(
            initialFlow,
            removeFromWatchlistFlow
        )
    }

    override fun WatchlistContract.PartialChange.getSingleEvent() = when (this) {
        is WatchlistContract.PartialChange.Removed -> WatchlistContract.ModelIntent.RemovedFromWatchlist
        else -> null
    }
}