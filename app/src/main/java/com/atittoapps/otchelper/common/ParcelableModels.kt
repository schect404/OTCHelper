package com.atittoapps.otchelper.common

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.atittoapps.domain.companies.model.DomainOfficer
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.HistoricalDataItem
import com.atittoapps.otchelper.R
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class HistoricalDataItemParcelable(
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Double,
    val date: Long
) : Parcelable {

    fun toDomain() = HistoricalDataItem(
        open, close, high, low, volume, date
    )

}

fun HistoricalDataItem.toParcel() = HistoricalDataItemParcelable(
    open, close, high, low, volume, date
)

@Parcelize
data class AppOfficer(
    val name: String?,
    val title: String?
): Parcelable {

    fun toDomain() = DomainOfficer(name, title)

}

fun DomainOfficer.toApp() = AppOfficer(name, title)

@Parcelize
data class DomainStockParcelable(
    val symbol: String?,
    val securityName: String? = null,
    val market: String? = null,
    val securityType: String? = null,
    val volume: Double? = null,
    val price: Double? = null,
    val outstandingShares: Long? = null,
    val outstandingSharesAsOfDate: Long? = null,
    val authorisedShares: Long? = null,
    val authorisedSharesAsOfDate: Long? = null,
    val restrictedShares: Long? = null,
    val restrictedSharesAsOfDate: Long? = null,
    val unrestrictedShares: Long? = null,
    val unrestrictedSharesAsOfDate: Long? = null,
    val publicFloat: Long? = null,
    val publicFloatAsOfDate: Long? = null,
    val dtcShares: Long? = null,
    val dtcSharesAsOfDate: Long? = null,
    val parValue: Double? = null,
    val previousClose: Double? = null,
    val openingPrice: Double? = null,
    val lastSale: Double? = null,
    val annualHigh: Double? = null,
    val annualLow: Double? = null,
    val isPriceGood: Boolean = false,
    val priceFirst: Double? = null,
    val isFavourite: Boolean = false,
    val isNew: Boolean? = false,
    val historicalData: List<HistoricalDataItemParcelable> = emptyList(),
    val lastPriceSaw: Double? = null,
    val isPriceChanged: Boolean = false,
    val fromLastMax: Double? = null,
    val name: String?,
    val city: String?,
    val country: String?,
    val website: String?,
    val businessDesc: String?,
    val email: String?,
    val numberOfEmployees: String?,
    val industry: String?,
    val marketCap: Double?,
    val estimatedMarketCapAsOfDate: Long? = null,
    val officers: List<AppOfficer> = listOf(),
    val alreadyLoaded: Boolean,
    val currentPossible: Boolean
) : Parcelable {

    fun toDomain() = DomainStock(
        symbol,
        securityName,
        market,
        securityType,
        volume,
        price,
        outstandingShares,
        outstandingSharesAsOfDate,
        authorisedShares,
        authorisedSharesAsOfDate,
        restrictedShares,
        restrictedSharesAsOfDate,
        unrestrictedShares,
        unrestrictedSharesAsOfDate,
        publicFloat,
        publicFloatAsOfDate,
        dtcShares,
        dtcSharesAsOfDate,
        parValue,
        previousClose,
        openingPrice,
        lastSale,
        annualHigh,
        annualLow,
        isPriceGood,
        priceFirst,
        isFavourite,
        isNew,
        historicalData.map { it.toDomain() },
        lastPriceSaw,
        isPriceChanged,
        fromLastMax,
        name,
        city,
        country,
        website,
        businessDesc,
        email,
        numberOfEmployees,
        industry,
        marketCap,
        estimatedMarketCapAsOfDate,
        officers.map { it.toDomain() },
        alreadyLoaded,
        currentPossible
    )

}

fun DomainStock.toParcel() = DomainStockParcelable(
    symbol,
    securityName,
    market,
    securityType,
    volume,
    price,
    outstandingShares,
    outstandingSharesAsOfDate,
    authorisedShares,
    authorisedSharesAsOfDate,
    restrictedShares,
    restrictedSharesAsOfDate,
    unrestrictedShares,
    unrestrictedSharesAsOfDate,
    publicFloat,
    publicFloatAsOfDate,
    dtcShares,
    dtcSharesAsOfDate,
    parValue,
    previousClose,
    openingPrice,
    lastSale,
    annualHigh,
    annualLow,
    isPriceGood,
    priceFirst,
    isFavourite,
    isNew,
    historicalData.map { it.toParcel() },
    lastPriceSaw,
    isPriceChanged,
    fromLastMax,
    name,
    city,
    country,
    website,
    businessDesc,
    email,
    numberOfEmployees,
    industry,
    marketCap,
    estimatedMarketCapAsOfDate,
    officers.map { it.toApp() },
    alreadyLoaded,
    currentPossible
)

data class LabelValue(
    val label: String,
    val value: String?,
    val addedInfo: String? = null
)

fun DomainStock.toInfoList(context: Context) = arrayListOf<LabelValue>().apply {
    name?.let { add(LabelValue(context.getString(R.string.name), it)) }
    industry?.let { add(LabelValue(context.getString(R.string.industry), it)) }
    businessDesc?.let { add(LabelValue(context.getString(R.string.description), it)) }
    numberOfEmployees?.let { add(LabelValue(context.getString(R.string.number_of_employees), it)) }
    officers.forEach {
        add(LabelValue(it.title ?: "", it.name))
    }
    country?.let { add(LabelValue(context.getString(R.string.country), it)) }
    city?.let { add(LabelValue(context.getString(R.string.city), it)) }
    website?.let { add(LabelValue(context.getString(R.string.website), it)) }
    email?.let { add(LabelValue(context.getString(R.string.email), it)) }

}

fun DomainStock.toLinksList(context: Context) = arrayListOf<LabelValue>().apply {
    symbol?.let { add(LabelValue(context.getString(R.string.twitter), "https://twitter.com/search?q=%24${it.toLowerCase()}&src=typed_query&f=live")) }
    symbol?.let { add(LabelValue(context.getString(R.string.reddit), "https://www.reddit.com/search/?q=%23${it}&sort=new")) }
    symbol?.let { add(LabelValue(context.getString(R.string.stocktwits), "https://stocktwits.com/symbol/${it}")) }
}

fun DomainStock.toSecuritiesList(context: Context) = arrayListOf<LabelValue>().apply {
    marketCap?.let { add(LabelValue(context.getString(R.string.market_cap), it.getWithDelimiters(0), estimatedMarketCapAsOfDate.getDateString()?.let { "(%s)".format(it) })) }
    authorisedShares?.let {
        add(
            LabelValue(
                context.getString(R.string.authorised),
                it.toDouble().getWithDelimiters(0),
                authorisedSharesAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }
    outstandingShares?.let {
        add(
            LabelValue(
                context.getString(R.string.outstanding),
                it.toDouble().getWithDelimiters(0),
                outstandingSharesAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }
    restrictedShares?.let {
        add(
            LabelValue(
                context.getString(R.string.restricted),
                it.toDouble().getWithDelimiters(0),
                restrictedSharesAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }
    unrestrictedShares?.let {
        add(
            LabelValue(
                context.getString(R.string.unrestricted),
                it.toDouble().getWithDelimiters(0),
                unrestrictedSharesAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }

    dtcShares?.let {
        add(
            LabelValue(
                context.getString(R.string.dtc),
                it.toDouble().getWithDelimiters(0),
                dtcSharesAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }
    publicFloat?.let {
        add(
            LabelValue(
                context.getString(R.string.float_shares),
                it.toDouble().getWithDelimiters(0),
                publicFloatAsOfDate.getDateString()?.let { "(%s)".format(it) }
            )
        )
    }
    parValue?.let {
        add(
            LabelValue(
                context.getString(R.string.parvalue),
                it.getWithDelimiters(4)
            )
        )
    }
}

fun Double.getWithDelimiters(numAfterPoint: Int = 2): String {
    return "%,.${numAfterPoint}f".format(Locale.FRANCE, this).replace(",", ".")
}

val formatter = SimpleDateFormat("dd.MM.yyyy")

fun Long?.getDateString() = this?.let { formatter.format(Date(it)) }

fun CrystalRangeSeekbar.rangeChanges() =
    callbackFlow {
        setOnRangeSeekbarChangeListener { minValue, maxValue ->
            offer(minValue to maxValue)
        }
        awaitClose { setOnRangeSeekbarChangeListener(null) }
    }

fun CrystalRangeSeekbar.rangeApplies() =
    callbackFlow {
        setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            offer(minValue to maxValue)
        }
        awaitClose { setOnRangeSeekbarFinalValueListener(null) }
    }