package com.example.otchelper.details.reports.sec

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.StubModelIntent
import com.example.data.prefs.SharedPrefsProvider
import com.example.domain.companies.model.DomainOtcNews
import com.example.domain.companies.model.DomainSecReport
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.BR
import com.example.otchelper.R
import com.example.otchelper.StubNavigator
import com.example.otchelper.base.BaseAppFragment
import com.example.otchelper.common.DomainStockParcelable
import com.example.otchelper.common.toParcel
import com.example.otchelper.databinding.FragmentMainBinding
import com.example.otchelper.databinding.ItemOtcNewsBinding
import com.example.otchelper.databinding.ItemReportBinding
import com.example.otchelper.databinding.ItemSkeletonNewsBinding
import com.example.otchelper.databinding.ItemSkeletonReportsBinding
import com.example.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.peridy.shimmerlayout.ShimmerGroup

class ReportsFragment :
    BaseAppFragment<ReportsContract.ViewIntent, StubModelIntent, ReportsContract.ViewState, ReportsContract.PartialChange>() {

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    private val isSec: Boolean
        get() = arguments?.getBoolean(IS_SEC) ?: false

    private val binding by viewBinding(FragmentMainBinding::bind)

    private val prefs: SharedPrefsProvider = get()

    override val actor: ReportsActor by viewModel()

    override val layoutRes = R.layout.fragment_main

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<ReportsItems>()
    private val shimmerGroup = ShimmerGroup()

    private val adapter = LastAdapter(items, BR.item)
        .map<ReportsItems.Sec, ItemReportBinding>(R.layout.item_report) {
            onClick { holder ->
                holder.binding.item?.sec?.let(::goUrl)
            }
        }
        .map<ReportsItems.Skeleton, ItemSkeletonReportsBinding>(R.layout.item_skeleton_reports) {
            onCreate {
                it.binding.itemShimmer.shimmerGroup = shimmerGroup
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
    }

    override fun intents() = merge(
        flowOf(stock?.let { ReportsContract.ViewIntent.Initial(it, isSec) }).filterNotNull()
    )

    override fun render(state: ReportsContract.ViewState) {
        super.render(state)
        items.update(state.news)
    }

    private fun goUrl(report: DomainSecReport) {
        if(isSec && report.filedAsHtml == false) return
        val browserIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    if (isSec) report.getUrl() else report.getUrlOtc()
                )
            )
        startActivity(browserIntent)
    }

    companion object {

        private const val STOCK = "stock"
        private const val IS_SEC = "is_sec"

        fun newInstanceSec(stock: DomainStock): ReportsFragment =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                    putBoolean(IS_SEC, true)
                }
            }

        fun newInstanceNotSec(stock: DomainStock): ReportsFragment =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                    putBoolean(IS_SEC, false)
                }
            }
    }
}