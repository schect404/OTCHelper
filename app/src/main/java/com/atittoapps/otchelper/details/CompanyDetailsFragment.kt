package com.atittoapps.otchelper.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.atittoapps.domain.companies.CompaniesInteractor
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.common.DomainStockParcelable
import com.atittoapps.otchelper.common.toParcel
import com.atittoapps.otchelper.companies.CompaniesBinding
import com.atittoapps.otchelper.databinding.FragmentDetailsBinding
import com.atittoapps.otchelper.viewBinding
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.get

class CompanyDetailsFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private var stock: DomainStock? = null

    override val layoutRes = R.layout.fragment_details

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<CompanyDetailsItems>()

    private val interactor: CompaniesInteractor = get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stock = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()
        val stockNotNull = stock ?: return
        binding.chart.historicalData = stockNotNull.historicalData
        binding.tvTicker.text = stockNotNull.symbol
        CompaniesBinding.isActualMoreThanFirst(binding.tvLastPrice, stockNotNull.lastSale)
        binding.tvMarket.text = stockNotNull.market
        binding.tvSharesType.text = stockNotNull.securityType

        binding.vpInfo.adapter = DetailsViewPagerAdapter(requireContext(), childFragmentManager, stockNotNull)

        binding.tabs.setupWithViewPager(binding.vpInfo)
        CompaniesBinding.isActiveImage(binding.ivMenu, stockNotNull.isFavourite)
        binding.ivMenu.setOnClickListener {
            val targetValue = !(stock?.isFavourite ?: false)
            CompaniesBinding.isActiveImage(binding.ivMenu, targetValue)
            stock = stock?.copy(isFavourite = targetValue)
            lifecycleScope.launchWhenStarted {
                stock?.let {
                    if (targetValue) {
                        interactor.addWatchlist(it).first()
                    } else interactor.removeFromWatchlist(it).first()
                }
            }
        }
    }

    companion object {

        private const val STOCK = "stock"

        fun newInstance(stock: DomainStock): CompanyDetailsFragment =
            CompanyDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                }
            }
    }

}