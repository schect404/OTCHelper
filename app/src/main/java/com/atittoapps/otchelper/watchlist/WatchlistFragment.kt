package com.atittoapps.otchelper.watchlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.base.BaseAppFragment
import com.atittoapps.otchelper.base.SwipeToDeleteCallback
import com.atittoapps.otchelper.databinding.FragmentMainBinding
import com.atittoapps.otchelper.databinding.ItemWatchlistBinding
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class WatchlistFragment :
    BaseAppFragment<WatchlistContract.ViewIntent, WatchlistContract.ModelIntent, WatchlistContract.ViewState, WatchlistContract.PartialChange>() {

    private val binding by viewBinding(FragmentMainBinding::bind)

    override val actor: WatchlistActor by viewModel()

    override val layoutRes = R.layout.fragment_main

    override val navigator: WatchlistNavigator = get()

    private val items = AsyncObservableList<DomainStock>()

    private val flow: MutableStateFlow<WatchlistContract.ViewIntent?> = MutableStateFlow(null)

    private val adapter = LastAdapter(items, BR.item)
        .map<DomainStock, ItemWatchlistBinding>(R.layout.item_watchlist) {
            onCreate { holder ->
                holder.binding.item?.let { stock ->
                    holder.binding.tvSymbol.setOnClickListener {
                        navigator.goDetails(stock, activity?.supportFragmentManager ?: requireFragmentManager())
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
        ItemTouchHelper(SwipeToDeleteCallback({
            flow.value = WatchlistContract.ViewIntent.RemoveFromWatchlist(items.list().get(it))
        }, getString(R.string.remove))).attachToRecyclerView(binding.rvStocks)
    }

    override fun intents() = merge(
        flowOf(WatchlistContract.ViewIntent.Initial),
        flow.filterNotNull()
    )

    override fun handleSingleEvent(event: WatchlistContract.ModelIntent) {
        when (event) {
            is WatchlistContract.ModelIntent.RemovedFromWatchlist -> showToast(getString(R.string.removed_from_watchlist))
        }
    }

    override fun render(state: WatchlistContract.ViewState) {
        super.render(state)
        items.update(state.items)
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance() = WatchlistFragment()
    }
}