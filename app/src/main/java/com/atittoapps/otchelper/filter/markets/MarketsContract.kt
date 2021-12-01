package com.atittoapps.otchelper.filter.markets

import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market

interface MarketsContract {

    sealed class ViewIntent : BaseViewIntent {
        class Initial(val currentChecked: List<Market>) : ViewIntent()
        class ItemChanged(val market: ChoosableMarket) : ViewIntent()
        class CheckAll(val isToSelect: Boolean): ViewIntent()
    }

    data class ViewState(
        val markets: List<ChoosableMarket> = listOf(),
        val allMarkets: List<ChoosableMarket> = listOf(),
        val isAllToCheck: Boolean = false
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {
        class Loaded(val allMarkets: List<Market>, val checkedMarkets: List<Market>) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val list = if(checkedMarkets.isNullOrEmpty()) {
                    allMarkets.map {
                        ChoosableMarket(it, true)
                    }
                } else {
                    allMarkets.map { industryToCheck -> ChoosableMarket(industryToCheck, checkedMarkets.firstOrNull { industryToCheck.id == it.id } != null) }
                }
                return initialState.copy(
                    markets = list,
                    allMarkets = list,
                    isAllToCheck = !list.allChecked()
                )
            }
        }

        class ItemChanged(val market: ChoosableMarket): PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val currentList = initialState.markets.map { if(it.market.id == market.market.id) market else it }
                val allList = initialState.allMarkets.map { if(it.market.id == market.market.id) market else it }
                return initialState.copy(
                    markets = currentList,
                    allMarkets = allList,
                    isAllToCheck = !allList.allChecked()
                )
            }
        }

        class CheckAll(val isToCheck: Boolean): PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(
                    markets = initialState.markets.map { it.copy(isChecked = isToCheck) },
                    allMarkets = initialState.allMarkets.map { it.copy(isChecked = isToCheck) },
                    isAllToCheck = !isToCheck
                )
        }
    }

}