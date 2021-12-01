package com.atittoapps.otchelper.filter

import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market
import com.atittoapps.domain.companies.model.SortingBy
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow

class FiltersUpdateDelegate {

    private val updateSortChannel = BroadcastChannel<SortingBy>(capacity = Channel.CONFLATED)

    val updateSortBy = updateSortChannel.asFlow()

    private val updateIndustriesChannel = BroadcastChannel<List<Industry>>(capacity = Channel.CONFLATED)

    val updateIndustry = updateIndustriesChannel.asFlow()

    private val updateMarketsChannel = BroadcastChannel<List<Market>>(capacity = Channel.CONFLATED)

    val updateMarkets = updateMarketsChannel.asFlow()

    val updateFlow = MutableStateFlow<Unit?>(null)

    fun update() {
        updateFlow.value = null
        updateFlow.value = Unit
    }

    suspend fun updateSortBy(sortingBy: SortingBy) {
        updateSortChannel.send(sortingBy)
    }

    suspend fun updateIndustries(industries: List<Industry>) {
        updateIndustriesChannel.send(industries)
    }

    suspend fun updateMarkets(markets: List<Market>) {
        updateMarketsChannel.send(markets)
    }
}