package com.atittoapps.otchelper.details.otcnews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.view.View
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.StubModelIntent
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.model.DomainOtcNews
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.base.BaseAppFragment
import com.atittoapps.otchelper.common.DomainStockParcelable
import com.atittoapps.otchelper.common.toParcel
import com.atittoapps.otchelper.databinding.FragmentMainBinding
import com.atittoapps.otchelper.databinding.ItemOtcNewsBinding
import com.atittoapps.otchelper.databinding.ItemSkeletonNewsBinding
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.peridy.shimmerlayout.ShimmerGroup

class OtcNewsFragment :
    BaseAppFragment<OtcNewsContract.ViewIntent, StubModelIntent, OtcNewsContract.ViewState, OtcNewsContract.PartialChange>() {

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    private val isExternal: Boolean
        get() = arguments?.getBoolean(IS_EXTERNAL) ?: false

    private val binding by viewBinding(FragmentMainBinding::bind)

    private val prefs: SharedPrefsProvider = get()

    override val actor: OtcNewsActor by viewModel()

    override val layoutRes = R.layout.fragment_main

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<OtcNewsItems>()
    private val shimmerGroup = ShimmerGroup()

    private val adapter = LastAdapter(items, BR.item)
        .map<OtcNewsItems.News, ItemOtcNewsBinding>(R.layout.item_otc_news) {
            onClick { holder ->
                holder.binding.item?.news?.let(::goUrl)
            }
        }
        .map<OtcNewsItems.Skeleton, ItemSkeletonNewsBinding>(R.layout.item_skeleton_news) {
            onCreate {
                it.binding.itemShimmer.shimmerGroup = shimmerGroup
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
    }

    override fun intents() = merge(
        flowOf(stock?.let { OtcNewsContract.ViewIntent.Initial(it, isExternal) }).filterNotNull()
    )

    override fun render(state: OtcNewsContract.ViewState) {
        super.render(state)
        items.update(state.news)
    }

    private fun goUrl(news: DomainOtcNews) {
        val uri = if (isExternal) news.getExternalNewsUrl(
                stock?.symbol ?: ""
        ) else news.getNewsUrl(stock?.symbol ?: "")

        if(uri.isNullOrEmpty()) return
        val browserIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    uri
                )
            )
        val headersBundle = Bundle().apply {
            putString("Origin", "https://www.otcmarkets.com")
            putString("Referer","https://www.otcmarkets.com/")
        }
        browserIntent.putExtra(Browser.EXTRA_HEADERS, headersBundle);
        startActivity(browserIntent)
    }

    companion object {

        private const val STOCK = "stock"
        private const val IS_EXTERNAL = "is_external"

        fun newInstanceExternal(stock: DomainStock): OtcNewsFragment =
            OtcNewsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                    putBoolean(IS_EXTERNAL, true)
                }
            }

        fun newInstanceNotExternal(stock: DomainStock): OtcNewsFragment =
            OtcNewsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                    putBoolean(IS_EXTERNAL, false)
                }
            }
    }
}