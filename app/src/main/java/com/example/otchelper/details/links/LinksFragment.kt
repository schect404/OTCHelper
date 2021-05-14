package com.example.otchelper.details.links

import android.os.Bundle
import android.view.View
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.BR
import com.example.otchelper.R
import com.example.otchelper.StubNavigator
import com.example.otchelper.common.DomainStockParcelable
import com.example.otchelper.common.LabelValue
import com.example.otchelper.common.toInfoList
import com.example.otchelper.common.toLinksList
import com.example.otchelper.common.toParcel
import com.example.otchelper.databinding.FragmentCompanyProfileBinding
import com.example.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import org.koin.android.ext.android.get

class LinksFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentCompanyProfileBinding::bind)

    private val stock: DomainStock?
        get() = arguments?.getParcelable<DomainStockParcelable>(STOCK)?.toDomain()

    override val layoutRes = R.layout.fragment_company_profile

    override val navigator: StubNavigator = get()

    private val items = AsyncObservableList<LabelValue>()

    private val adapter = LastAdapter(items, BR.item)
        .map<LabelValue>(R.layout.item_lab_val)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDetails.attachAdapter(adapter)
        items.update(stock?.toLinksList() ?: listOf())
    }

    companion object {

        private const val STOCK = "stock"

        fun newInstance(stock: DomainStock): LinksFragment =
            LinksFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK, stock.toParcel())
                }
            }
    }
}