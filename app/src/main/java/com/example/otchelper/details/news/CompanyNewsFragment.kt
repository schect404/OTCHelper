package com.example.otchelper.details.news

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R
import com.example.otchelper.StubNavigator
import com.example.otchelper.common.DomainStockParcelable
import com.example.otchelper.common.toParcel
import com.example.otchelper.databinding.FragmentNewsBinding
import com.example.otchelper.details.CompanyDetailsItems
import com.example.otchelper.viewBinding
import org.koin.android.ext.android.get

class CompanyNewsFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentNewsBinding::bind)

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    override val layoutRes = R.layout.fragment_news

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<CompanyDetailsItems>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stockNotNull = stock ?: return

        binding.vpInfo.adapter =
            NewsViewPagerAdapter(requireContext(), childFragmentManager, stockNotNull)

        binding.tabs.setupWithViewPager(binding.vpInfo)
    }

    companion object {

        private const val STOCK = "stock"

        fun newInstance(stock: DomainStock): CompanyNewsFragment =
            CompanyNewsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                }
            }
    }

}