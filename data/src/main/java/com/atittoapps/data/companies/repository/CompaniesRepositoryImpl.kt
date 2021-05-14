package com.atittoapps.data.companies.repository

import com.atittoapps.data.companies.api.CompaniesApi
import com.atittoapps.data.companies.conversions.toDomain
import com.atittoapps.data.companies.dao.StocksDao
import com.atittoapps.data.companies.model.CompanyDetails
import com.atittoapps.data.companies.model.Stocks
import com.atittoapps.data.companies.model.toData
import com.atittoapps.data.companies.model.toDbStock
import com.atittoapps.data.companies.model.toWatchlist
import com.atittoapps.data.prefs.SharedPrefsProvider
import com.atittoapps.domain.companies.CompaniesRepository
import com.atittoapps.domain.companies.model.DomainFilters
import com.atittoapps.domain.companies.model.DomainOtcNews
import com.atittoapps.domain.companies.model.DomainSecReport
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.DomainSymbols
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.zip
import java.text.DecimalFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CompaniesRepositoryImpl(
    private val api: CompaniesApi,
    private val gson: Gson,
    private val stocksDao: StocksDao,
    private val sharedPrefsProvider: SharedPrefsProvider
) : CompaniesRepository {

    private val format = DecimalFormat("##0.####")

    override fun fetchPage(page: Int) = flow {
        val filters = sharedPrefsProvider.getFilters()
        val string = api.filterCompanies(
            filters.priceRange.min,
            filters.priceRange.max,
            filters.minVolume,
            filters.getMarketsString(),
            page
        )
        val parsed = gson.fromJson(string, Stocks::class.java)
        val domainList = parsed.toDomain(page)
        emit(domainList)
    }.flatMapConcat { pageFetched ->
        getCompanyFullProfile(pageFetched.shares).map { pageFetched.copy(shares = it) }
    }.map {
        val filters = sharedPrefsProvider.getFilters()
        it.copy(shares = it.shares.filter {
            it.isFullyAppropriate(
                filters.shouldShowWithNoSharesNumberInformation,
                filters.toDomainFilters().floatRange
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
    }

    override fun getOtcReports(stock: DomainStock) = flow {
        val reports = api.getOtcReports(stock.symbol, stock.symbol).records?.map { it.toDomain() }
        emit(reports ?: listOf<DomainSecReport>())
    }

    override fun filterCompanies() = flow {
        val filters = sharedPrefsProvider.getFilters()
        val string = api.filterCompanies(
            filters.priceRange.min,
            filters.priceRange.max,
            filters.minVolume,
            filters.getMarketsString()
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
                    filters.toDomainFilters().floatRange
                )
            }
        }

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
                    isNew = (old.firstOrNull { it.symbol == stock.symbol } == null) || (old.firstOrNull { it.symbol == stock.symbol }?.isNew == true)
                )
            )
        }
        emit(listResult)
    }

    fun getOneCompanyFullProfile(stock: DomainStock) = flow {
        val profile = api.getCompanyFullProfile(stock.symbol, stock.symbol)
        emit(profile)
    }.delayEach(100).catch {
        emit(CompanyDetails(null))
    }

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
        val historicalData = api.getHistoricalData(
            stock.symbol,
            TimeUnit.MILLISECONDS.toSeconds(calendar.timeInMillis - TimeUnit.DAYS.toMillis(180))
                .toString(),
            TimeUnit.MILLISECONDS.toSeconds(calendar.timeInMillis).toString()
        ).toDataItemList()
        emit(
            stock.copy(
                historicalData = historicalData
            )
        )
    }
        .catch { emit(stock.copy(historicalData = listOf())) }
        .retry(2)

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
        sharedPrefsProvider.putFilters(filters.toData())
        sharedPrefsProvider.putIsFirstTime(true)
    }

    override fun getSymbols() = flow {
        val symbols = api.getSymbols().map { it.toDomain() }
        emit(symbols)
    }

    override fun getCompanyProfile(symbols: DomainSymbols) = flow {
        emit(DomainStock(symbol = symbols.ticker, securityName = symbols.fullName))
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