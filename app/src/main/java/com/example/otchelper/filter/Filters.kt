package com.example.otchelper.filter

import androidx.annotation.StringRes
import com.example.domain.companies.model.DomainFilters
import com.example.domain.companies.model.DomainRange
import com.example.otchelper.R

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

    data class PushesSound (
        override val title: Int,
        var value: Boolean,
        override val id: Int = 5
    ) : Filters(title, id)
}

fun DomainFilters.toList() = listOf(
    Filters.ShouldShowRestOfPink(R.string.markets, shouldShowRestOfPink),
    Filters.Price(R.string.price, priceRange),
    Filters.Volume(R.string.volume, minVolume.toString()),
    Filters.Float(R.string.float_dtc_shares, floatRange),
    Filters.ShouldSkipNoSharesNumberInfo(R.string.shares_number, shouldShowWithNoSharesNumberInformation),
    Filters.PushesSound(R.string.sound_of_notifications, shouldPushesSound)
)

fun List<Filters>.toDomain() = DomainFilters(
    filterIsInstance<Filters.ShouldShowRestOfPink>().first().value,
    filterIsInstance<Filters.Price>().first().range,
    filterIsInstance<Filters.Volume>().first().value.toLong(),
    filterIsInstance<Filters.Float>().first().range,
    filterIsInstance<Filters.ShouldSkipNoSharesNumberInfo>().first().value,
    filterIsInstance<Filters.PushesSound>().first().value
)



