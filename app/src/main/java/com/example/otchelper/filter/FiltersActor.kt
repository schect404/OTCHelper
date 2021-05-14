package com.example.otchelper.filter

import com.atitto.mviflowarch.base.BaseActor
import com.example.domain.companies.CompaniesInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class FiltersActor(private val companiesInteractor: CompaniesInteractor) :
    BaseActor<FiltersContract.ViewIntent, FiltersContract.ModelIntent, FiltersContract.ViewState, FiltersContract.PartialChange>() {

    override val initialState = FiltersContract.ViewState()

    override fun Flow<FiltersContract.ViewIntent>.handleIntent(): Flow<FiltersContract.PartialChange> {

        val initialFlow = filterIsInstance<FiltersContract.ViewIntent.Initial>()
            .map { companiesInteractor.getFilters().toList() }
            .map { FiltersContract.PartialChange.Loaded(it) }

        val changedFlow = filterIsInstance<FiltersContract.ViewIntent.FiltersChanged>()
            .map { FiltersContract.PartialChange.Changes(it.filter) }

        val savedFlow = filterIsInstance<FiltersContract.ViewIntent.Save>()
            .map {
                val list = viewState?.value?.listOfFilters ?: listOf()
                companiesInteractor.storeFilters(list.toDomain())
                FiltersContract.PartialChange.Saved
            }

        return merge(
            initialFlow,
            changedFlow,
            savedFlow
        )
    }

    override fun FiltersContract.PartialChange.getSingleEvent() = when (this) {
        is FiltersContract.PartialChange.Saved -> FiltersContract.ModelIntent.Saved
        else -> null
    }

}