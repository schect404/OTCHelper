package com.atittoapps.otchelper.filter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.atitto.mviflowarch.base.BaseFragment
import com.atitto.mviflowarch.extensions.*
import com.atitto.mviflowarch.list.AsyncObservableList
import com.atittoapps.otchelper.BR
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.StubNavigator
import com.atittoapps.otchelper.databinding.FragmentFiltersBinding
import com.atittoapps.otchelper.databinding.ItemFloatFilterBinding
import com.atittoapps.otchelper.databinding.ItemMarketsFilterBinding
import com.atittoapps.otchelper.databinding.ItemNoSharesInfoFilterBinding
import com.atittoapps.otchelper.databinding.ItemPriceFilterBinding
import com.atittoapps.otchelper.databinding.ItemPushesSoundSettingBinding
import com.atittoapps.otchelper.databinding.ItemVolumeFilterBinding
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

    override val navigator: StubNavigator = get()

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
                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.value = it
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
                lifecycleScope.launchWhenStarted {
                    holder.binding.etFrom.textChanges()
                        .debounce(DEBOUNCE_TIME)
                        .onEach {
                            holder.binding.item?.range?.copy(min = it.toLongOrNull())
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
                            holder.binding.item?.range?.copy(max = it.toLongOrNull())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStocks.attachAdapter(adapter)
        lifecycleScope.launchWhenStarted {
            binding.fabSave.clicks()
                .onEach { flow.value = FiltersContract.ViewIntent.Save }
                .collect()
        }
    }

    override fun intents() = merge(
        flowOf(FiltersContract.ViewIntent.Initial),
        flow.filterNotNull()
    )

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
        private const val DEBOUNCE_TIME = 100L
    }

}