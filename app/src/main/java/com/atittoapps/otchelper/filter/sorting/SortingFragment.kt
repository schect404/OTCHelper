package com.atittoapps.otchelper.filter.sorting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.atitto.mviflowarch.extensions.attachAdapter
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atitto.mviflowarch.stub.SimpleFragment
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.databinding.FragmentListBinding
import com.atittoapps.otchelper.databinding.FragmentListWithSearchBinding
import com.atittoapps.otchelper.databinding.ItemChoosableSortingBinding
import com.atittoapps.otchelper.filter.FiltersUpdateDelegate
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class SortingFragment : SimpleFragment() {

    private val binding by viewBinding(FragmentListBinding::bind)

    override val layoutRes = R.layout.fragment_list

    override val navigator: StubNavigator = get()

    private val sharedPrefsProvider: SharedPrefsProvider = get()

    private val nowChosen: AppSortingBy
    get() = arguments?.getParcelable(NOW_CHOSEN) ?: AppSortingBy.PRICE_ASCENDING

    private val updateDelegate: FiltersUpdateDelegate = get()

    private val items = AsyncObservableList<ChoosableSorting>()

    private val adapter = LastAdapter(items, BR.item)
            .map<ChoosableSorting, ItemChoosableSortingBinding>(R.layout.item_choosable_sorting) {
                onClick { it.binding.item?.let(::choose) }
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvTitle.text = getString(R.string.sort_by)
            rvStocks.attachAdapter(adapter)
            fabSave.setOnClickListener {
                navigator.back()
            }
        }
        items.update(AppSortingBy.createChoosableList(nowChosen.toDomain()))
    }

    private fun choose(sort: ChoosableSorting) {
        val currentList = items.list()
        items.update(currentList.map { it.copy(isChosen = sort.sorting == it.sorting) })
        lifecycleScope.launch {
            updateDelegate.updateSortBy(sort.sorting.toDomain())
        }
    }

    companion object {

        private const val NOW_CHOSEN = "now_chosen"

        fun newInstance(nowChosen: AppSortingBy) = SortingFragment().apply {
            arguments = Bundle().apply {
                putParcelable(NOW_CHOSEN, nowChosen)
            }
        }
    }

}