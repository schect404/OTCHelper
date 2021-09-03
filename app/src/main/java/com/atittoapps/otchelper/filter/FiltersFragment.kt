package com.atittoapps.otchelper.filter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.atitto.mviflowarch.base.BaseFragment
import com.atitto.mviflowarch.extensions.*
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atittoapps.domain.companies.model.DomainRange
import com.atittoapps.domain.companies.model.Range
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.common.rangeApplies
import com.atittoapps.otchelper.common.rangeChanges
import com.atittoapps.otchelper.databinding.*
import com.atittoapps.otchelper.filter.sorting.AppSortingBy
import com.atittoapps.otchelper.filter.sorting.toApp
import com.atittoapps.otchelper.viewBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersFragment :
    BaseFragment<FiltersContract.ViewIntent, FiltersContract.ModelIntent, FiltersContract.ViewState, FiltersContract.PartialChange>() {

    private val binding by viewBinding(FragmentFiltersBinding::bind)

    override val actor: FiltersActor by viewModel()

    override val layoutRes = R.layout.fragment_filters

    override val navigator: FiltersNavigator = get()

    private val filtersUpdateDelegate: FiltersUpdateDelegate = get()

    private val items = AsyncObservableList<Filters>()

    private val flow: MutableStateFlow<FiltersContract.ViewIntent?> = MutableStateFlow(null)

    private val adapter = LastAdapter(items, BR.item)
        .map<Filters.ShouldShowRestOfPink, ItemMarketsFilterBinding>(R.layout.item_markets_filter) {
            onCreate { holder ->
                lifecycleScope.launchWhenStarted {
                    holder.binding.cbInclude.checkChanges()
                        .onEach {
                            holder.binding.item?.value = it
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it)
                            }
                        }
                        .collect()
                }
            }
        }
        .map<Filters.ShouldSkipNoSharesNumberInfo, ItemNoSharesInfoFilterBinding>(R.layout.item_no_shares_info_filter) {
            onCreate { holder ->
                lifecycleScope.launchWhenStarted {
                    holder.binding.cbInclude.checkChanges()
                        .onEach {
                            holder.binding.item?.value = it
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it.copy())
                            }
                        }
                        .collect()
                }
            }
        }
        .map<Filters.Volume, ItemVolumeFilterBinding>(R.layout.item_volume_filter) {
            onCreate { holder ->
                holder.binding.etFrom.addTextChangedListener(SeparatorTextWatcher(holder.binding.etFrom))
                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.value = it.replace(" ", "")
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it.copy())
                            }
                        }
                        .collect()
                }
            }
            onBind {
                val selection = it.binding.etFrom.selectionStart
                it.binding.etFrom.setText(it.binding.item?.value)
                //if(selection <= it.binding.etFrom.text?.length ?: 0) it.binding.etFrom.setSelection(selection)
            }
        }
        .map<Filters.Price, ItemPriceFilterBinding>(R.layout.item_price_filter) {
            onCreate { holder ->

                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(
                                min = it.replace(",", ".").toDoubleOrNull()
                            )?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it)
                            }
                        }
                        .collect()
                }


                lifecycleScope.launchWhenStarted {
                    holder.binding.etTo.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(
                                max = it.replace(",", ".").toDoubleOrNull()
                            )?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it)
                            }
                        }
                        .collect()
                }
            }
        }
        .map<Filters.Float, ItemFloatFilterBinding>(R.layout.item_float_filter) {
            onCreate { holder ->
                holder.binding.etFrom.addTextChangedListener(SeparatorTextWatcher(holder.binding.etFrom))
                holder.binding.etTo.addTextChangedListener(SeparatorTextWatcher(holder.binding.etTo))
                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(min = it.replace(" ", "").toLongOrNull())
                                ?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it)
                            }
                        }
                        .collect()
                }

                lifecycleScope.launchWhenStarted {
                    holder.binding.etTo.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(max = it.replace(" ", "").toLongOrNull())
                                ?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it.copy())
                            }
                        }
                        .collect()
                }
            }
            onBind {
                val selectionFrom = it.binding.etFrom.selectionStart
                val selectionTo = it.binding.etTo.selectionStart
                it.binding.etFrom.setText(it.binding.item?.range?.min?.toString())
                it.binding.etTo.setText(it.binding.item?.range?.max?.toString())
            }
        }
        .map<Filters.AuthorizedShares, ItemAsFilterBinding>(R.layout.item_as_filter) {
            onCreate { holder ->
                holder.binding.etFrom.addTextChangedListener(SeparatorTextWatcher(holder.binding.etFrom))
                holder.binding.etTo.addTextChangedListener(SeparatorTextWatcher(holder.binding.etTo))
                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(min = it.replace(" ", "").toLongOrNull())
                                ?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it)
                            }
                        }
                        .collect()
                }

                lifecycleScope.launchWhenStarted {
                    holder.binding.etTo.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(max = it.replace(" ", "").toLongOrNull())
                                ?.let { holder.binding.item?.range = it }
                            holder.binding.item?.isError =
                                holder.binding.item?.getWithValidation()?.isError ?: false
                            holder.binding.error.visibleIfOrGone {
                                holder.binding.item?.isError ?: false
                            }
                            flow.value = null
                            flow.value = holder.binding.item?.let {
                                FiltersContract.ViewIntent.FiltersChanged(it.copy())
                            }
                        }
                        .collect()
                }
            }
            onBind {
                val selectionFrom = it.binding.etFrom.selectionStart
                val selectionTo = it.binding.etTo.selectionStart
                it.binding.etFrom.setText(it.binding.item?.range?.min?.toString())
                it.binding.etTo.setText(it.binding.item?.range?.max?.toString())
            }
        }
        .map<Filters.SortBy, ItemFilterSortBinding>(R.layout.item_filter_sort) {
            onClick {
                navigator.goSorting(
                    it.binding.item?.sortingBy ?: AppSortingBy.PRICE_ASCENDING,
                    activity?.supportFragmentManager ?: requireFragmentManager()
                )
            }
        }
        .map<Filters.Industries, ItemFilterIndustryBinding>(R.layout.item_filter_industry) {
            onClick {
                navigator.goIndustries(
                    it.binding.item?.industries ?: listOf(),
                    activity?.supportFragmentManager ?: requireFragmentManager()
                )
            }
        }
        .map<Filters.PushesSound, ItemPushesSoundSettingBinding>(R.layout.item_pushes_sound_setting) {
            onCreate {
                it.binding.scChecked.setOnCheckedChangeListener { buttonView, isChecked ->
                    flow.value = null
                    flow.value = it.binding.item?.let {
                        it.value = isChecked
                        FiltersContract.ViewIntent.FiltersChanged(it)
                    }
                }
            }
        }
        .map<Filters.RangeShares, ItemRangeFilterBinding>(R.layout.item_range_filter) {
            onCreate { holder ->
                lifecycleScope.launchWhenStarted {
                    holder.binding.rangeSeekbar.rangeApplies()
                        .onEach {
                            holder.binding.item?.onSelectRange(it)
                        }
                        .collect()
                }

                lifecycleScope.launchWhenStarted {
                    holder.binding.rangeSeekbar.rangeChanges()
                        .onEach {
                            holder.binding.tvMin.text = "%s%s".format(it.first.toString(), "%")
                            holder.binding.tvMax.text = "%s%s".format(it.second.toString(), "%")
                        }
                        .collect()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
        lifecycleScope.launchWhenStarted {
            binding.fabSave.clicks()
                .onEach { flow.value = FiltersContract.ViewIntent.Save }
                .collect()
        }

        lifecycleScope.launchWhenStarted {
            filtersUpdateDelegate.updateSortBy
                .onEach {
                    it ?: return@onEach
                    items.list().filterIsInstance<Filters.SortBy>().firstOrNull()?.copy(
                        sortingBy = it.toApp()
                    )?.let {
                        flow.value = FiltersContract.ViewIntent.FiltersChanged(it)
                    }
                }.collect()
        }

        lifecycleScope.launchWhenStarted {
            filtersUpdateDelegate.updateIndustry
                .onEach {
                    it ?: return@onEach
                    items.list().filterIsInstance<Filters.Industries>().firstOrNull()?.copy(
                        industries = it
                    )?.let {
                        flow.value = FiltersContract.ViewIntent.FiltersChanged(it)
                    }
                }.collect()
        }
    }

    private fun Filters.RangeShares.onSelectRange(range: Pair<Number, Number>) {
        setRangeCurrent(DomainRange(range.first.toInt(), range.second.toInt()))
        flow.value = FiltersContract.ViewIntent.FiltersChanged(this)
    }

    override fun intents() = merge(
        flowOf(FiltersContract.ViewIntent.Initial),
        flow.filterNotNull()
    )

    override fun onResume() {
        super.onResume()
        flow.value = FiltersContract.ViewIntent.Initial
    }

    override fun handleSingleEvent(event: FiltersContract.ModelIntent) {
        when (event) {
            is FiltersContract.ModelIntent.Saved -> showToast(getString(R.string.saved))
        }
    }

    override fun render(state: FiltersContract.ViewState) {
        super.render(state)
        items.update(state.listOfFilters)
        binding.fabSave.enableIf { state.buttonEnabled }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance() = FiltersFragment()
        private const val DEBOUNCE_TIME = 400L
    }

}