package com.atittoapps.otchelper.filter.markets

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.atitto.mviflowarch.base.BaseActor
import com.atitto.mviflowarch.base.BaseFragment
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atitto.mviflowarch.stub.StubViewIntent
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market
import com.atittoapps.domain.companies.model.SortingBy
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.databinding.*
import com.atittoapps.otchelper.filter.FiltersUpdateDelegate
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarketsFragment : BaseFragment<MarketsContract.ViewIntent, StubModelIntent, MarketsContract.ViewState, MarketsContract.PartialChange>() {

    private val binding by viewBinding(FragmentListWithSearchBinding::bind)

    override val layoutRes = R.layout.fragment_list_with_search

    override val navigator: StubNavigator = get()

    override val actor: MarketsActor by viewModel()

    private val sharedPrefsProvider: SharedPrefsProvider = get()

    private val nowChosen: List<ParcelableMarket>
    get() = arguments?.getParcelableArrayList(NOW_CHOSEN) ?: listOf()

    private val updateDelegate: FiltersUpdateDelegate = get()

    private val items = AsyncObservableList<ChoosableMarket>()

    private val channel = BroadcastChannel<MarketsContract.ViewIntent>(capacity = Channel.CONFLATED)

    private val flow = channel.asFlow()

    private val adapter = LastAdapter(items, BR.item)
            .map<ChoosableMarket, ItemChoosableMarketBinding>(R.layout.item_choosable_market) {
                onClick { it.binding.item?.let { choose(it.copy(isChecked = !it.isChecked)) } }
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvTitle.text = getString(R.string.select_markets)
            rvStocks.attachAdapter(adapter)
            fabSave.setOnClickListener {
                navigator.back()
            }
            tvSelectAll.setOnClickListener {
                val isToSelectAll = actor.viewState?.value?.isAllToCheck ?: true
                lifecycleScope.launch {
                    val allChecked = actor.viewState?.value?.allMarkets ?: listOf()
                    updateDelegate.updateMarkets(allChecked.map { it.market })
                    channel.send(MarketsContract.ViewIntent.CheckAll(isToSelectAll))
                }
            }
        }
    }

    override fun intents() = merge(
        flowOf(MarketsContract.ViewIntent.Initial(nowChosen.map { it.toDomain() })),
        flow
    )

    override fun render(state: MarketsContract.ViewState) {
        super.render(state)
        items.update(state.markets)
        binding.tvSelectAll.text = if(state.isAllToCheck) getString(R.string.select_all) else getString(R.string.unselect_all)
    }

    private fun choose(ind: ChoosableMarket) {
        val allChecked = actor.viewState?.value?.allMarkets?.map { if(it.market.id == ind.market.id) ind else it }?.filter { it.isChecked } ?: listOf()
        lifecycleScope.launch {
            updateDelegate.updateMarkets(allChecked.map { it.market })
            channel.send(MarketsContract.ViewIntent.ItemChanged(ind))
        }
    }

    companion object {

        private const val NOW_CHOSEN = "now_chosen"

        fun newInstance(nowChosen: List<Market>) = MarketsFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(NOW_CHOSEN, ArrayList(nowChosen.map { it.toParcelable() }))
            }
        }
    }

}