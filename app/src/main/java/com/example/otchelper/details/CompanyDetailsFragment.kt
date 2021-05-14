package com.example.otchelper.details

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.StubNavigator
import com.example.otchelper.common.DomainStockParcelable
import com.example.otchelper.common.toParcel
import com.example.otchelper.companies.CompaniesBinding
import com.example.otchelper.databinding.FragmentDetailsBinding
import com.example.otchelper.viewBinding
import org.koin.android.ext.android.get

class CompanyDetailsFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    override val layoutRes = R.layout.fragment_details

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<CompanyDetailsItems>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stockNotNull = stock ?: return
        binding.chart.historicalData = stockNotNull.historicalData
        binding.tvTicker.text = stockNotNull.symbol
        CompaniesBinding.isActualMoreThanFirst(binding.tvLastPrice, stockNotNull.lastSale)
        binding.tvMarket.text = stockNotNull.market
        binding.tvSharesType.text = stockNotNull.securityType

        binding.vpInfo.adapter = DetailsViewPagerAdapter(requireContext(), childFragmentManager, stockNotNull)

        binding.tabs.setupWithViewPager(binding.vpInfo)
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