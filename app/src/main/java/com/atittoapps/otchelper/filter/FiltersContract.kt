package com.atittoapps.otchelper.filter

import com.atitto.mviflowarch.base.BaseModelIntent
import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState

interface FiltersContract {

    sealed class ViewIntent : BaseViewIntent {

        object Initial : ViewIntent()
        class FiltersChanged(val filter: Filters) : ViewIntent()
        object Save : ViewIntent()
    }

    sealed class ModelIntent : BaseModelIntent {
        object Saved : ModelIntent()
    }

    data class ViewState(
        val listOfFilters: List<Filters> = listOf(),
        val buttonEnabled: Boolean = false
    ): BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        class Loaded(val filters: List<Filters>) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                return initialState.copy(
                    listOfFilters = filters,
                    buttonEnabled = filters.filter { it.isValid().not() }.isEmpty()
                )
            }
        }

        class Changes(val filter: Filters) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val filters = initialState.listOfFilters.map {
                    if (it.id == filter.id) filter.getWithValidation() else it
                }
                return initialState.copy(
                    listOfFilters = filters,
                    buttonEnabled = filters.filter { it.isValid().not() }.isEmpty()
                )
            }
        }

        object Saved : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState.copy(buttonEnabled = false)
        }
    }
}