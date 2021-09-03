package com.atittoapps.otchelper.details.levels

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.base.BaseAppFragment
import com.atittoapps.otchelper.common.DomainStockParcelable
import com.atittoapps.otchelper.common.toParcel
import com.atittoapps.otchelper.databinding.FragmentLevelsBinding
import com.atittoapps.otchelper.databinding.ItemLevelBinding
import com.atittoapps.otchelper.databinding.ItemSkeletonLevelsBinding
import com.atittoapps.otchelper.databinding.ItemSkeletonNewsBinding
import com.atittoapps.otchelper.details.otcnews.OtcNewsItems
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.peridy.shimmerlayout.ShimmerGroup

class LevelsFragment : BaseAppFragment<LevelsContract.ViewIntent, StubModelIntent, LevelsContract.ViewState, LevelsContract.PartialChange>() {

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    private val binding by viewBinding(FragmentLevelsBinding::bind)

    private val prefs: SharedPrefsProvider = get()

    override val actor: LevelsActor by viewModel()

    override val layoutRes = R.layout.fragment_levels

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<LevelsItems>()
    private val shimmerGroup = ShimmerGroup()

    private val adapter = LastAdapter(items, BR.item)
            .map<LevelsItems.Level, ItemLevelBinding>(R.layout.item_level)
            .map<LevelsItems.Skeleton, ItemSkeletonLevelsBinding>(R.layout.item_skeleton_levels) {
                onCreate {
                    it.binding.itemShimmer.shimmerGroup = shimmerGroup
                }
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
    }

    override fun intents() = merge(
            flowOf(stock?.let { LevelsContract.ViewIntent.Initial(it) }).filterNotNull()
    )

    override fun render(state: LevelsContract.ViewState) {
        super.render(state)
        items.update(state.levels)
    }

    companion object {

        private const val STOCK = "stock"

        fun newInstance(stock: DomainStock): LevelsFragment =
                LevelsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(STOCK, stock.toParcel())
                    }
                }

    }
}