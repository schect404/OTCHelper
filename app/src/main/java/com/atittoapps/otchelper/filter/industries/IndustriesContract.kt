package com.atittoapps.otchelper.filter.industries

import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.atittoapps.domain.companies.model.Industry

interface IndustriesContract {

    sealed class ViewIntent : BaseViewIntent {
        class Initial(val currentChecked: List<Industry>) : ViewIntent()
        class ItemChanged(val industry: ChoosableIndustry) : ViewIntent()
        class CheckAll(val isToSelect: Boolean): ViewIntent()
    }

    data class ViewState(
        val industries: List<ChoosableIndustry> = listOf(),
        val allIndustries: List<ChoosableIndustry> = listOf(),
        val isAllToCheck: Boolean = false
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {
        class Loaded(val allIndustries: List<Industry>, val checkedIndustries: List<Industry>) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val list = if(checkedIndustries.isNullOrEmpty()) {
                    allIndustries.map {
                        ChoosableIndustry(it, true)
                    }
                } else {
                    allIndustries.map { industryToCheck -> ChoosableIndustry(industryToCheck, checkedIndustries.firstOrNull { industryToCheck.id == it.id } != null) }
                }
                return initialState.copy(
                    industries = list,
                    allIndustries = list,
                    isAllToCheck = !list.allChecked()
                )
            }
        }

        class ItemChanged(val industry: ChoosableIndustry): PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val currentList = initialState.industries.map { if(it.industry.id == industry.industry.id) industry else it }
                val allList = initialState.allIndustries.map { if(it.industry.id == industry.industry.id) industry else it }
                return initialState.copy(
                    industries = currentList,
                    allIndustries = allList,
                    isAllToCheck = !allList.allChecked()
                )
            }
        }

        class CheckAll(val isToCheck: Boolean): PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(
                    industries = initialState.industries.map { it.copy(isChecked = isToCheck) },
                    allIndustries = initialState.allIndustries.map { it.copy(isChecked = isToCheck) },
                    isAllToCheck = !isToCheck
                )
        }
    }

}