package com.atittoapps.otchelper.search

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.extensions.textChanges
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.base.BaseAppFragment
import com.atittoapps.otchelper.databinding.FragmentSearchBinding
import com.atittoapps.otchelper.databinding.ItemLabValSearchBinding
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.peridy.shimmerlayout.ShimmerGroup

class SearchFragment :
    BaseAppFragment<SearchContract.ViewIntent, SearchContract.ModelIntent, SearchContract.ViewState, SearchContract.PartialChange>() {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val prefs: SharedPrefsProvider = get()

    override val actor: SearchActor by viewModel()

    override val layoutRes = R.layout.fragment_search

    override val navigator: SearchNavigator = get()

    private val items = AsyncObservableList<SearchItems>()

    private val flow: MutableStateFlow<SearchContract.ViewIntent?> = MutableStateFlow(null)

    private val shimmerGroup = ShimmerGroup()

    private val adapter = LastAdapter(items, BR.item)
        .map<SearchItems.Result, ItemLabValSearchBinding>(R.layout.item_lab_val_search) {
            onClick {
                it.binding.item?.let {
                    flow.value = SearchContract.ViewIntent.GoToDetails(it.symbol)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
    }

    override fun intents() = merge(
        flowOf(SearchContract.ViewIntent.Initial),
        binding.etSearch.textChanges().debounce(400).map { SearchContract.ViewIntent.Search(it) },
        flow.filterNotNull()
    )

    override fun handleSingleEvent(event: SearchContract.ModelIntent) {
        when (event) {
            is SearchContract.ModelIntent.GoToDetails ->
                navigator.goDetails(event.stock, activity?.supportFragmentManager ?: requireFragmentManager())
        }
    }

    override fun render(state: SearchContract.ViewState) {
        super.render(state)
        items.update(state.currentList)
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}