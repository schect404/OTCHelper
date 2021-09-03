package com.atittoapps.otchelper.filter.sorting

import android.os.Parcelable
import androidx.annotation.StringRes
import com.atittoapps.data.companies.model.DataSortingBy
import com.atittoapps.domain.companies.model.SortingBy
import com.atittoapps.otchelper.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AppSortingBy(@StringRes val title: Int): Parcelable {
    PRICE_ASCENDING(R.string.price_ascending),
    PRICE_DESCENDING(R.string.price_descending),
    MARKET_ASCENDING(R.string.market_ascending),
    MARKET_DESCENDING(R.string.market_descending),
    VOLUME_ASCENDING(R.string.volume_ascending),
    VOLUME_DESCENDING(R.string.volume_descending);

    fun toDomain() = when (this) {
        PRICE_ASCENDING -> SortingBy.PRICE_ASCENDING
        PRICE_DESCENDING -> SortingBy.PRICE_DESCENDING
        MARKET_ASCENDING -> SortingBy.MARKET_ASCENDING
        MARKET_DESCENDING -> SortingBy.MARKET_DESCENDING
        VOLUME_ASCENDING -> SortingBy.VOLUME_ASCENDING
        VOLUME_DESCENDING -> SortingBy.VOLUME_DESCENDING
    }

    companion object {
        fun createChoosableList(chosen: SortingBy) = values().map { ChoosableSorting(it, it == chosen.toApp()) }
    }
}

fun SortingBy.toApp() = when(this) {
    SortingBy.PRICE_ASCENDING -> AppSortingBy.PRICE_ASCENDING
    SortingBy.PRICE_DESCENDING -> AppSortingBy.PRICE_DESCENDING
    SortingBy.MARKET_ASCENDING -> AppSortingBy.MARKET_ASCENDING
    SortingBy.MARKET_DESCENDING -> AppSortingBy.MARKET_DESCENDING
    SortingBy.VOLUME_ASCENDING -> AppSortingBy.VOLUME_ASCENDING
    SortingBy.VOLUME_DESCENDING -> AppSortingBy.VOLUME_DESCENDING
}