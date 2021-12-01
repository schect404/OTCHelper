package com.atittoapps.otchelper.filter

import androidx.annotation.StringRes
import com.atittoapps.domain.companies.model.DomainFilters
import com.atittoapps.domain.companies.model.DomainRange
import com.atittoapps.domain.companies.model.Industry
import com.atittoapps.domain.companies.model.Market
import com.atittoapps.otchelper.R
import com.atittoapps.otchelper.filter.sorting.AppSortingBy
import com.atittoapps.otchelper.filter.sorting.toApp

sealed class Filters(
    @StringRes open val title: Int,
    open val id: Int
) {

    open fun isValid() = true

    open fun getWithValidation() = this

    data class Price(
        override val title: Int,
        var range: DomainRange<Double>,
        var isError: Boolean = false,
        override val id: Int = 0
    ) : Filters(title, id) {

        override fun isValid(): Boolean {

            val minNotNull = range.min ?: return true
            val maxNotNull = range.max ?: return true
            return minNotNull <= maxNotNull
        }

        override fun getWithValidation(): Filters.Price {
            isError = !isValid()
            return this
        }
    }

    data class AuthorizedShares(
        override val title: Int,
        var range: DomainRange<Long>,
        var isError: Boolean = false,
        override val id: Int = 9
    ) : Filters(title, id) {

        override fun isValid(): Boolean {
            val minNotNull = range.min ?: return true
            val maxNotNull = range.max ?: return true
            return minNotNull <= maxNotNull
        }

        override fun getWithValidation(): Filters.AuthorizedShares {
            isError = !isValid()
            return this
        }
    }

    data class Float(
        override val title: Int,
        var range: DomainRange<Long>,
        var isError: Boolean = false,
        override val id: Int = 1
    ) : Filters(title, id) {

        override fun isValid(): Boolean {
            val minNotNull = range.min ?: return true
            val maxNotNull = range.max ?: return true
            return minNotNull <= maxNotNull
        }

        override fun getWithValidation(): Filters.Float {
            isError = !isValid()
            return this
        }
    }

    data class ShouldSkipNoSharesNumberInfo(
        override val title: Int,
        var value: Boolean,
        override val id: Int = 2
    ) : Filters(title, id)

    data class ShouldShowRestOfPink(
        override val title: Int,
        var value: Boolean,
        override val id: Int = 3
    ) : Filters(title, id)

    data class Volume(
        override val title: Int,
        var value: String,
        var isError: Boolean = false,
        override val id: Int = 4
    ) : Filters(title, id) {

        override fun isValid(): Boolean {
            value.toLongOrNull() ?: return false
            return true
        }

        override fun getWithValidation(): Filters.Volume {
            isError = !isValid()
            return this
        }
    }

    data class PushesSound(
        override val title: Int,
        var value: Boolean,
        override val id: Int = 6
    ) : Filters(title, id)

    data class SortBy(
            override val title: Int,
            override val id: Int = 5,
            val sortingBy: AppSortingBy
    ) : Filters(title, id)

    data class Industries(
            override val title: Int,
            override val id: Int = 7,
            val industries: List<Industry>
    ) : Filters(title, id)

    data class Markets(
            override val title: Int,
            override val id: Int = 10,
            val markets: List<Market>
    ) : Filters(title, id)

    data class RangeShares(
            override val title: Int,
            val rangeMax: DomainRange<Int>,
            private var rangeCurrentPrivate: DomainRange<Int>,
            override val id: Int = 8
    ) : Filters(title, id) {

        val rangeCurrent
            get() = rangeCurrentPrivate

        fun setRangeCurrent(rangeCurrent: DomainRange<Int>) {
            rangeCurrentPrivate = rangeCurrent
        }

        fun getMin() = "%s%s".format(rangeCurrentPrivate.getMinText()?.toFloat(), "%")

        fun getMax() = "${rangeCurrentPrivate.getMaxText()}&#x0025;"
    }
}

fun DomainFilters.toList() = listOf(
        Filters.Markets(R.string.markets, markets = markets),
        Filters.SortBy(R.string.sort_by, sortingBy = sortingBy.toApp()),
        Filters.Industries(R.string.industries, industries = industries),
        Filters.Price(R.string.price, priceRange),
        Filters.RangeShares(R.string.auth_out, DomainRange(0, 100), sharesRange),
        Filters.Volume(R.string.volume, minVolume.toString()),
        Filters.AuthorizedShares(R.string.authorised, asRange),
        Filters.Float(R.string.float_dtc_shares, floatRange),
        Filters.ShouldSkipNoSharesNumberInfo(
                R.string.shares_number,
                shouldShowWithNoSharesNumberInformation
        ),
        Filters.PushesSound(R.string.sound_of_notifications, shouldPushesSound)
)

fun List<Filters>.toDomain() = DomainFilters(
        false,
        filterIsInstance<Filters.Price>().first().range,
        filterIsInstance<Filters.Volume>().first().value.toLong(),
        filterIsInstance<Filters.Float>().first().range,
        filterIsInstance<Filters.ShouldSkipNoSharesNumberInfo>().first().value,
        filterIsInstance<Filters.PushesSound>().first().value,
        filterIsInstance<Filters.SortBy>().first().sortingBy.toDomain(),
        filterIsInstance<Filters.Industries>().first().industries,
        filterIsInstance<Filters.RangeShares>().first().rangeCurrent,
        filterIsInstance<Filters.AuthorizedShares>().first().range,
        filterIsInstance<Filters.Markets>().first().markets
)



