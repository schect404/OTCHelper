package com.atittoapps.data.companies.repository

import android.content.Context
import androidx.annotation.RawRes
import com.atittoapps.data.R
import com.atittoapps.data.companies.api.CompaniesApi
import com.atittoapps.data.companies.conversions.toDomain
import com.atittoapps.data.companies.dao.StocksDao
import com.atittoapps.data.companies.model.*
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.CompaniesRepository
import com.atittoapps.domain.companies.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CompaniesRepositoryImpl(
    private val api: CompaniesApi,
    private val gson: Gson,
    private val stocksDao: StocksDao,
    private val sharedPrefsProvider: SharedPrefsProvider,
    private val context: Context
) : CompaniesRepository {

    private val format = DecimalFormat("##0.####")

    private inline fun <reified T> readRawJson(@RawRes rawResId: Int): T {
        context.resources.openRawResource(rawResId).bufferedReader().use {
            return gson.fromJson<T>(it, object : TypeToken<T>() {}.type)
        }
    }

    override fun getAllIndustries(): List<Industry> {
        return readRawJson<List<DataIndustry>>(R.raw.industries).map { it.toDomain() }
    }

    override fun getAllMarkets(): List<Market> {
        return readRawJson<List<DataMarket>>(R.raw.markets).map { it.toDomain() }
    }

    override fun getPrimaryFiltered() = flow {
        val filters = sharedPrefsProvider.getFilters()
        val lastUpdated = sharedPrefsProvider.getLastUpdated()
        val now = Calendar.getInstance().timeInMillis
        val change = (now - lastUpdated)
        val currentList = stocksDao.getFullStocks().map { it.toDomain(gson) }
        if (currentList.isNullOrEmpty() || ((change > 48 * 3600 * 1000) && (lastUpdated != 0)) || (lastUpdated == 0)) {

                val string = api.filterCompanies(
                    filters.priceRange.min,
                    filters.priceRange.max,
                    filters.minVolume,
                    filters.getMarketsString(getAllMarkets().map { it.toData() }),
                    filters.getIndustriesString().toString()
                )
                val parsed = gson.fromJson(string, Stocks::class.java)
                val domainList = parsed.stocks?.map { it.toDomain() } ?: listOf()
            stocksDao.addToFiltered(domainList.map { it.toDbStock(gson) })
            sharedPrefsProvider.putLastUpdated(now)
            emit(domainList)
        } else emit(currentList)
    }.catch {
        val dbData = stocksDao.getFullStocks().map { it.toDomain(gson) }
        if (dbData.isEmpty()) throw Exception("OTC Markets currently unavailable")
        emit(dbData)
    }

    override fun getLevels(stock: DomainStock) = flow {
        val levels = api.getLevels(stock.symbol).levels ?: listOf()
        emit(levels.map { format.format(it) })
    }

    override fun updateCache(stocks: List<DomainStock>) = flow {
        stocksDao.removeAllFromFiltered()
        stocksDao.addToFiltered(stocks.map { it.toDbStock(gson) })
        emit(Unit)
    }

    override fun fetchPage(page: Int) = flow {
        val filters = sharedPrefsProvider.getFilters()
        val string = api.filterCompanies(
            filters.priceRange.min,
            filters.priceRange.max,
            filters.minVolume,
            filters.getMarketsString(getAllMarkets().map { it.toData() })
        )
        val parsed = gson.fromJson(string, Stocks::class.java)
        val domainList = parsed.toDomain(page)
        emit(domainList)
    }

        .flatMapConcat { pageFetched ->
        getCompanyFullProfile(pageFetched.shares).map { pageFetched.copy(shares = it) }
    }.map {
        val filters = sharedPrefsProvider.getFilters()
        it.copy(shares = it.shares.filter {
            it.isFullyAppropriate(
                filters.shouldShowWithNoSharesNumberInformation,
                filters.toDomainFilters().floatRange,
                filters.toDomainFilters().sharesRange,
                filters.toDomainFilters().asRange
            )
        })
    }.flatMapConcat {
        val listResult = arrayListOf<DomainStock>()
        it.shares.forEach { stock ->
            val withHistoricalData = getCompaniesInside(stock)
                .zip(getHistoricalData(stock)) { inside, historicalData ->
                    stock.copy(
                        annualHigh = inside.annualHigh,
                        annualLow = inside.annualLow,
                        lastSale = inside.lastSale,
                        openingPrice = inside.openingPrice,
                        previousClose = inside.previousClose,
                        historicalData = historicalData.historicalData
                    )
                }.flatMapConcat {
                    getSecReports(stock).zip(getOtcReports(stock)) { secReps, otcReps ->
                        val now = Calendar.getInstance().timeInMillis
                        val edgeTime: Long = now - (1000L * 3600L * 24L * 30L * 3L)
                        val areNewSec =
                            secReps.firstOrNull { it.receivedDate ?: 0 >= edgeTime } != null
                        val areNewOtc =
                            otcReps.firstOrNull { it.receivedDate ?: 0 >= edgeTime } != null
                        return@zip it.copy(
                            currentPossible = (areNewOtc || areNewSec) && ((stock.market == "Pink No Information") || (stock.market == "Pink Limited"))
                        )
                    }
                }.map {
                    val maxFromPrev =
                        if (it.historicalData.size > 2) it.historicalData.subList(
                            0,
                            it.historicalData.size - 2
                        ).maxByOrNull {
                            it.high
                        } ?: return@map it else return@map it
                    val last = it.lastSale ?: return@map it
                    val fromLastMax = (last - maxFromPrev.high) / maxFromPrev.high
                    it.copy(fromLastMax = fromLastMax)
                }.retry(3).first()

            listResult.add(withHistoricalData)
        }
        flowOf(it.copy(shares = listResult.map { it.copy(isPriceGood = it.isPriceAppropriate()) }))
    }

    override fun getIsCurrentPossibleBasedOnReports(stock: DomainStock) =
        getSecReports(stock).zip(getOtcReports(stock)) { secReps, otcReps ->
            val now = Calendar.getInstance().timeInMillis
            val edgeTime: Long = now - (1000L * 3600L * 24L * 30L * 3L)
            val areNewSec =
                secReps.firstOrNull { it.receivedDate ?: 0 >= edgeTime } != null
            val areNewOtc =
                otcReps.firstOrNull { it.receivedDate ?: 0 >= edgeTime } != null
            return@zip stock.copy(
                currentPossible = (areNewOtc || areNewSec) && ((stock.market == "Pink No Information") || (stock.market == "Pink Limited"))
            )
        }

    override fun getFilteredCompanies() = flow {
        val favourites = stocksDao.getFullWatchlist().map { it.toDomain() }
        val listOfFiltered = stocksDao.getFullStocks().map { it.toDomain(gson) }
            .map { stock -> stock.copy(isFavourite = favourites.firstOrNull { it.symbol == stock.symbol } != null) }
        stocksDao.removeAllFromFiltered()
        stocksDao.addToFiltered(listOfFiltered.map { it.copy(isNew = false).toDbStock(gson) })
        emit(listOfFiltered)
    }

    override fun getOtcCompanyNews(stock: DomainStock) = flow {
        val news = api.getCompanyOtcNews(stock.symbol, stock.symbol).toDomain()
        emit(news ?: listOf<DomainOtcNews>())
    }.catch { emit(listOf<DomainOtcNews>()) }

    override fun getExternalCompanyNews(stock: DomainStock) = flow {
        val news = api.getCompanyExternalNews(stock.symbol, stock.symbol).toDomain()
        emit(news ?: listOf<DomainOtcNews>())
    }.catch { emit(listOf<DomainOtcNews>()) }

    override fun getSecReports(stock: DomainStock) = flow {
        val reports = api.getSecReports(stock.symbol, stock.symbol).records?.map { it.toDomain() }
        emit(reports ?: listOf<DomainSecReport>())
    }.catch { emit(listOf()) }

    override fun getOtcReports(stock: DomainStock) = flow {
        val reports = api.getOtcReports(stock.symbol, stock.symbol).records?.map { it.toDomain() }
        emit(reports ?: listOf<DomainSecReport>())
    }.catch { emit(listOf()) }

    override fun filterCompanies() = flow {
        val filters = sharedPrefsProvider.getFilters()
        val string = api.filterCompanies(
            filters.priceRange.min,
            filters.priceRange.max,
            filters.minVolume,
            filters.getMarketsString(getAllMarkets().map { it.toData() })
        )
        val parsed = gson.fromJson(string, Stocks::class.java)
        val domainList = parsed.toDomain().shares ?: listOf()
        emit(domainList)
    }.flatMapConcat {
        getCompanyFullProfile(it)
    }
        .map {
            val filters = sharedPrefsProvider.getFilters()
            it.filter {
                it.isFullyAppropriate(
                    filters.shouldShowWithNoSharesNumberInformation,
                    filters.toDomainFilters().floatRange,
                    filters.toDomainFilters().sharesRange,
                    filters.toDomainFilters().asRange
                )
            }
        }

    override fun getFullCompany(stock: DomainStock) =
        getOneCompanyFullProfile(stock).map { profile ->
            val favourites = stocksDao.getFullWatchlist().map { it.toDomain() }
            val security = profile.securities?.firstOrNull()
            stock.copy(
                name = profile.name,
                city = profile.city,
                country = profile.country,
                website = profile.website,
                businessDesc = profile.businessDesc,
                email = profile.email,
                numberOfEmployees = profile.numberOfEmployees,
                authorisedShares = security?.authorisedShares,
                authorisedSharesAsOfDate = security?.authorisedSharesAsOfDate,
                outstandingShares = security?.outstandingShares,
                outstandingSharesAsOfDate = security?.outstandingSharesAsOfDate,
                publicFloat = security?.publicFloat,
                publicFloatAsOfDate = security?.publicFloatAsOfDate,
                dtcShares = security?.dtcShares,
                dtcSharesAsOfDate = security?.dtcSharesAsOfDate,
                restrictedShares = security?.restrictedShares,
                restrictedSharesAsOfDate = security?.restrictedSharesAsOfDate,
                unrestrictedShares = security?.unrestrictedShares,
                unrestrictedSharesAsOfDate = security?.unrestrictedSharesAsOfDate,
                securityType = security?.typeName,
                market = security?.tierName,
                parValue = security?.parValue,
                marketCap = profile?.estimatedMarketCap,
                estimatedMarketCapAsOfDate = profile?.estimatedMarketCapAsOfDate,
                isFavourite = favourites.firstOrNull { it.symbol == stock.symbol } != null,
                officers = profile.officers?.map { DomainOfficer(it.name, it.title) }
                    ?: listOf()
            )
        }.map {
            val filters = sharedPrefsProvider.getFilters().toDomainFilters()
            it.copy(
                compliantToShareStructureFilter = it.isFullyAppropriate(
                    filters.shouldShowWithNoSharesNumberInformation,
                    filters.floatRange,
                    filters.sharesRange,
                    filters.asRange
                )
            )
        }.retry(3)


    fun getCompanyFullProfile(stocks: List<DomainStock>) = flow {
        val listResult = arrayListOf<DomainStock>()
        val favourites = stocksDao.getFullWatchlist().map { it.toDomain() }
        val old = stocksDao.getFullStocks()
        stocks.forEach { stock ->
            val profile = getOneCompanyFullProfile(stock).first()
            val security = profile.securities?.firstOrNull()
            listResult.add(
                stock.copy(
                    name = profile.name,
                    city = profile.city,
                    country = profile.country,
                    website = profile.website,
                    businessDesc = profile.businessDesc,
                    email = profile.email,
                    numberOfEmployees = profile.numberOfEmployees,
                    authorisedShares = security?.authorisedShares,
                    authorisedSharesAsOfDate = security?.authorisedSharesAsOfDate,
                    outstandingShares = security?.outstandingShares,
                    outstandingSharesAsOfDate = security?.outstandingSharesAsOfDate,
                    publicFloat = security?.publicFloat,
                    publicFloatAsOfDate = security?.publicFloatAsOfDate,
                    dtcShares = security?.dtcShares,
                    dtcSharesAsOfDate = security?.dtcSharesAsOfDate,
                    restrictedShares = security?.restrictedShares,
                    restrictedSharesAsOfDate = security?.restrictedSharesAsOfDate,
                    unrestrictedShares = security?.unrestrictedShares,
                    unrestrictedSharesAsOfDate = security?.unrestrictedSharesAsOfDate,
                    securityType = security?.typeName,
                    market = security?.tierName,
                    parValue = security?.parValue,
                    marketCap = profile?.estimatedMarketCap,
                    estimatedMarketCapAsOfDate = profile?.estimatedMarketCapAsOfDate,
                    isFavourite = favourites.firstOrNull { it.symbol == stock.symbol } != null,
                    officers = profile.officers?.map { DomainOfficer(it.name, it.title) }
                        ?: listOf(),
                    isNew = (old.firstOrNull { it.symbol == stock.symbol } == null) || (old.firstOrNull { it.symbol == stock.symbol }?.isNew == true)
                )
            )
        }
        emit(listResult)
    }

    fun getOneCompanyFullProfile(stock: DomainStock) = flow {
        val profile = api.getCompanyFullProfile(stock.symbol, stock.symbol)
        emit(profile)
    }.delayEach(400).catch {
        emit(CompanyDetails(null))
    }.retry(3)

    override fun getCompaniesInside(stock: DomainStock) = flow {
        val inside = api.getCompanyInside(stock.symbol, stock.symbol)
        emit(
            stock.copy(
                annualHigh = inside.annualHigh,
                annualLow = inside.annualLow,
                lastSale = inside.lastSale,
                openingPrice = inside.openingPrice,
                previousClose = inside.previousClose
            )
        )
    }
        .catch { emit(stock) }.retry(2)

    override fun getHistoricalData(stock: DomainStock) = flow {
        val calendar = Calendar.getInstance()
        val historicalData = kotlinx.coroutines.withTimeoutOrNull(8000) {
            api.getHistoricalData(
                stock.symbol,
                TimeUnit.MILLISECONDS.toSeconds(calendar.timeInMillis - TimeUnit.DAYS.toMillis(180))
                    .toString(),
                (TimeUnit.MILLISECONDS.toSeconds(calendar.timeInMillis - TimeUnit.HOURS.toMillis(12))).toString()
            )
        }
        historicalData?.let {
            stocksDao.addToHistoricalData(
                DbHistoricalData(
                    stock.symbol ?: "",
                    historicalData.toStringJson(gson)
                )
            )
            emit(
                stock.copy(
                    historicalData = historicalData.toDataItemList()
                )
            )
        } ?: run { throw Exception() }

    }.catch {
        val data = stocksDao.getHistoricalData(stock.symbol ?: "").firstOrNull()
            ?.toDataHistoricalData(gson)?.toDataItemList() ?: listOf()
        emit(stock.copy(historicalData = data))
    }.map {
        val maxFromPrev =
            if (it.historicalData.size > 2) it.historicalData.subList(
                0,
                it.historicalData.size - 2
            ).maxByOrNull {
                it.high
            } ?: return@map it else return@map it
        val last = it.lastSale ?: return@map it
        val fromLastMax = (last - maxFromPrev.high) / maxFromPrev.high
        it.copy(fromLastMax = fromLastMax)
    }

    override fun getWatchlist(fromApp: Boolean) = flow {
        val favourites = stocksDao.getFullWatchlist().map { it.toDomain() }
        emit(favourites.map {
            val info = api.getCompanyInside(it.symbol, it.symbol)
            val historicalData = getHistoricalData(it).first().historicalData
            it.copy(
                annualHigh = info.annualHigh,
                annualLow = info.annualLow,
                lastSale = info.lastSale,
                openingPrice = info.openingPrice,
                previousClose = info.previousClose,
                historicalData = historicalData,
                lastPriceSaw = if (fromApp) info.lastSale else it.lastPriceSaw,
                isPriceChanged = info.lastSale != it.lastPriceSaw
            )
        }.map {
            it.copy(isPriceGood = it.isPriceAppropriate())
        })
    }.flatMapConcat {
        getCompanyFullProfile(it)
    }.flatMapConcat {
        stocksDao.removeAllFromWatchlist()
        stocksDao.addWatchlist(it.map { it.toWatchlist() })
        flowOf(it)
    }

    override fun addWatchlist(stock: DomainStock) = flow {
        stocksDao.addToWatchlist(stock.toWatchlist())
        emit(stock)
    }

    override fun removeFromWatchlist(stock: DomainStock) = flow {
        stocksDao.removeFromWatchlist(stock.toWatchlist())
        emit(stock)
    }

    override suspend fun storeNewFiltered(stocks: List<DomainStock>) {
        stocksDao.removeAllFromFiltered()
        stocksDao.addToFiltered(stocks.map { it.toDbStock(gson) })
    }

    override fun getFilters() = sharedPrefsProvider.getFilters().toDomainFilters()

    override fun storeFilters(filters: DomainFilters) {
        runBlocking {
            stocksDao.removeAllFromFiltered()
        }
        sharedPrefsProvider.putLastUpdated(0)
        sharedPrefsProvider.putFilters(filters.toData())
        sharedPrefsProvider.putIsFirstTime(true)
    }

    override fun getSymbols() = flow {
        val symbols = api.getSymbols().map { it.toDomain() }
        emit(symbols)
    }

    override fun getCompanyProfile(symbols: DomainSymbols) = flow {
        val favourites = stocksDao.getFullWatchlist().map { it.toDomain() }
        emit(
            DomainStock(
                symbol = symbols.ticker,
                securityName = symbols.fullName,
                isFavourite = favourites.firstOrNull { it.symbol == symbols.ticker } != null))
    }.flatMapConcat {
        getCompanyFullProfile(listOf(it)).map { it.firstOrNull() }.filterNotNull()
    }.flatMapConcat { stock ->
        getCompaniesInside(stock)
            .zip(getHistoricalData(stock)) { inside, historicalData ->
                stock.copy(
                    annualHigh = inside.annualHigh,
                    annualLow = inside.annualLow,
                    lastSale = inside.lastSale,
                    openingPrice = inside.openingPrice,
                    previousClose = inside.previousClose,
                    historicalData = historicalData.historicalData
                )
            }.map {
                val maxFromPrev =
                    if (it.historicalData.size > 2) it.historicalData.subList(
                        0,
                        it.historicalData.size - 2
                    ).maxByOrNull {
                        it.high
                    } ?: return@map it else return@map it
                val last = it.lastSale ?: return@map it
                val fromLastMax = (last - maxFromPrev.high) / maxFromPrev.high
                it.copy(fromLastMax = fromLastMax)
            }.retry(3)
    }
}