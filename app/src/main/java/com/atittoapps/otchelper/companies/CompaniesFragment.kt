package com.atittoapps.otchelper.companies

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.base.BaseAppFragment
import com.atittoapps.otchelper.base.paging
import com.atittoapps.otchelper.databinding.FragmentMainBinding
import com.atittoapps.otchelper.databinding.ItemSkeletonBinding
import com.atittoapps.otchelper.databinding.ItemStockBinding
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.peridy.shimmerlayout.ShimmerGroup

class CompaniesFragment :
    BaseAppFragment<CompaniesContract.ViewIntent, CompaniesContract.ModelIntent, CompaniesContract.ViewState, CompaniesContract.PartialChange>() {

    private val binding by viewBinding(FragmentMainBinding::bind)

    private val prefs: SharedPrefsProvider = get()

    override val actor: CompaniesActor by viewModel()

    override val layoutRes = R.layout.fragment_main

    override val navigator: CompaniesNavigator = get()

    private val items = AsyncObservableList<CompaniesItems>()

    private val flow: MutableStateFlow<CompaniesContract.ViewIntent?> = MutableStateFlow(null)

    private val shimmerGroup = ShimmerGroup()

    private val adapter = LastAdapter(items, BR.item)
        .map<CompaniesItems.Stock, ItemStockBinding>(R.layout.item_stock) {
            onCreate { holder ->
                holder.binding.tvSymbol.setOnClickListener {
                    holder.binding.item?.let { navigator.goDetails(it.domainStock, activity?.supportFragmentManager ?: requireFragmentManager()) }
                }
                holder.binding.ivMenu.setOnClickListener {
                    holder.binding.item?.let {
                        flow.value =
                            if (it.domainStock.isFavourite) CompaniesContract.ViewIntent.RemoveFromWatchList(it.domainStock)
                            else CompaniesContract.ViewIntent.AddToWatchList(it.domainStock)
                    }
                }
            }
        }
        .map<CompaniesItems.Skeleton, ItemSkeletonBinding>(R.layout.item_skeleton) {
            onCreate {
                it.binding.itemShimmer.shimmerGroup = shimmerGroup
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
    }

    override fun intents() = merge(
        flowOf(CompaniesContract.ViewIntent.InitialFilter),
        flow.filterNotNull()
    )

    override fun handleSingleEvent(event: CompaniesContract.ModelIntent) {
        when (event) {
            is CompaniesContract.ModelIntent.AddedToWatchlist -> showToast(getString(R.string.added_to_watchlist))
            is CompaniesContract.ModelIntent.RemoveFromWatchlist -> showToast(getString(R.string.removed_from_watchlist))
        }
    }

    override fun render(state: CompaniesContract.ViewState) {
        super.render(state)
        items.update(state.items)
        if(state.shouldLoad) {
            flow.value = CompaniesContract.ViewIntent.LoadByFiltered(state.items.filterIsInstance<CompaniesItems.Stock>().map { it.domainStock })
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance() = CompaniesFragment()
    }
}