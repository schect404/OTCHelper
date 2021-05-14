package com.example.otchelper.companies

import com.atitto.mviflowarch.base.BaseModelIntent
import com.atitto.mviflowarch.base.BasePartialChange
import com.atitto.mviflowarch.base.BaseViewIntent
import com.atitto.mviflowarch.base.BaseViewState
import com.example.domain.companies.model.DomainStock

interface CompaniesContract {

    sealed class ViewIntent : BaseViewIntent {

        object LoadPage : ViewIntent()
        object InitialNotFirst : ViewIntent()
        class AddToWatchList(val stock: DomainStock) : ViewIntent()
        class RemoveFromWatchList(val stock: DomainStock) : ViewIntent()
    }

    sealed class ModelIntent : BaseModelIntent {

        object AddedToWatchlist : ModelIntent()
        object RemoveFromWatchlist : ModelIntent()
    }

    data class ViewState(
        val nextPage: Int? = 0,
        val items: List<CompaniesItems> = listOf(),
        val progressVisible: Boolean = true
    ) : BaseViewState

    sealed class PartialChange : BasePartialChange<ViewState> {

        class Loaded(
            private val items: List<DomainStock>,
            private val page: Int?
        ) : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(
                    items = initialState.items.filterNot { it is CompaniesItems.Skeleton }.toMutableList().apply { addAll(items.map { CompaniesItems.Stock(it) }) },
                    nextPage = page,
                    progressVisible = false
                )
        }

        object LoadedSkeleton : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(
                    items = initialState.items.toMutableList()
                        .apply { addAll(CompaniesItems.Skeleton.createSkeletons()) })
        }

        object RemoveSkeleton : PartialChange() {
            override fun reduceToState(initialState: ViewState) =
                initialState.copy(
                    items = initialState.items.toMutableList().filterNot { it is CompaniesItems.Skeleton })
        }

        object Stub : PartialChange() {
            override fun reduceToState(initialState: ViewState) = initialState
        }

        class AddedToWatchlist(private val item: DomainStock) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val list = initialState.items.filterIsInstance<CompaniesItems.Stock>().map {
                    if (it.domainStock.symbol == item.symbol) it.copy(
                        domainStock = it.domainStock.copy(
                            isFavourite = true
                        )
                    )
                    else it
                }
                return initialState.copy(items = list)
            }
        }

        class RemovedFromWatchlist(private val item: DomainStock) : PartialChange() {
            override fun reduceToState(initialState: ViewState): ViewState {
                val list = initialState.items.filterIsInstance<CompaniesItems.Stock>().map {
                    if (it.domainStock.symbol == item.symbol) it.copy(
                        domainStock = it.domainStock.copy(
                            isFavourite = false
                        )
                    )
                    else it
                }
                return initialState.copy(items = list)
            }
        }
    }

}