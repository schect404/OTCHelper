package com.atittoapps.domain.companies

import com.atittoapps.domain.companies.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.zip

interface CompaniesInteractor {

    fun fetchPage(page: Int): Flow<SharesPage>

    fun getPrimaryFiltered(): Flow<List<DomainStock>>

    fun getFilteredCompanies(): Flow<List<DomainStock>>

    fun filterCompanies(): Flow<List<DomainStock>>

    fun getWatchlist(fromApp: Boolean): Flow<List<DomainStock>>

    fun addWatchlist(stock: DomainStock): Flow<DomainStock>

    fun removeFromWatchlist(stock: DomainStock): Flow<DomainStock>

    fun getFilters(): DomainFilters

    fun storeFilters(filters: DomainFilters)

    fun getOtcCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getExternalCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getSecReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getOtcReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getSymbols(): Flow<List<DomainSymbols>>

    fun getCompanyProfile(symbols: DomainSymbols): Flow<DomainStock>

    fun getIsCurrentPossibleBasedOnReports(stock: DomainStock): Flow<DomainStock>

    fun getHistoricalData(stock: DomainStock): Flow<DomainStock>

    fun getFullCompany(stock: DomainStock): Flow<DomainStock>

    fun updateCache(stocks: List<DomainStock>): Flow<Unit>

    fun getLevels(stock: DomainStock): Flow<List<String>>

    fun getAllIndustries(): List<Industry>
}

class CompaniesInteractorImpl(
    private val companiesRepository: CompaniesRepository
) : CompaniesInteractor {

    override fun fetchPage(page: Int) =
        companiesRepository.fetchPage(page)
            .map { it.copy(shares = it.shares.sortedBy { it.lastSale }) }

    override fun getAllIndustries() = companiesRepository.getAllIndustries()

    override fun getPrimaryFiltered() = companiesRepository.getPrimaryFiltered().map {
        val currentSort = companiesRepository.getFilters().sortingBy
        when(currentSort) {
            SortingBy.PRICE_ASCENDING -> it.sortedBy { it.lastSale }
            SortingBy.PRICE_DESCENDING -> it.sortedByDescending { it.lastSale }
            SortingBy.MARKET_DESCENDING -> it.sortedByDescending { it.market }
            SortingBy.MARKET_ASCENDING -> it.sortedBy { it.market }
            SortingBy.VOLUME_DESCENDING -> it.sortedByDescending { it.volume }
            SortingBy.VOLUME_ASCENDING -> it.sortedBy { it.volume }
        }
    }

    override fun updateCache(stocks: List<DomainStock>) = companiesRepository.updateCache(stocks)

    override fun getFullCompany(stock: DomainStock) = companiesRepository.getFullCompany(stock)

    override fun getHistoricalData(stock: DomainStock) =
        companiesRepository.getHistoricalData(stock)

    override fun getIsCurrentPossibleBasedOnReports(stock: DomainStock) =
        companiesRepository.getIsCurrentPossibleBasedOnReports(stock)

    override fun getLevels(stock: DomainStock) = companiesRepository.getLevels(stock)

    override fun filterCompanies() =
        companiesRepository.filterCompanies()
            .flatMapConcat {
                val listResult = arrayListOf<DomainStock>()
                it.forEach { stock ->
                    val withHistoricalData = companiesRepository.getCompaniesInside(stock)
                        .zip(companiesRepository.getHistoricalData(stock)) { inside, historicalData ->
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
                companiesRepository.storeNewFiltered(listResult.map { it.copy(isPriceGood = it.isPriceAppropriate()) })
                flowOf(listResult.map { it.copy(isPriceGood = it.isPriceAppropriate()) })
            }
            .map { it.sortedBy { it.lastSale } }

    override fun getFilteredCompanies() =
        companiesRepository.getFilteredCompanies()
            .map {
                it.map {
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
            }
            .map { it.sortedWith(compareByDescending<DomainStock> { it.isPriceGood }.thenBy { it.price }) }

    override fun addWatchlist(stock: DomainStock) = companiesRepository.addWatchlist(stock)

    override fun getWatchlist(fromApp: Boolean) = companiesRepository.getWatchlist(fromApp)

    override fun removeFromWatchlist(stock: DomainStock) =
        companiesRepository.removeFromWatchlist(stock)

    override fun getFilters() = companiesRepository.getFilters()

    override fun storeFilters(filters: DomainFilters) {
        companiesRepository.storeFilters(filters)
    }

    override fun getOtcCompanyNews(stock: DomainStock) =
        companiesRepository.getOtcCompanyNews(stock)

    override fun getExternalCompanyNews(stock: DomainStock) =
        companiesRepository.getExternalCompanyNews(stock)

    override fun getSecReports(stock: DomainStock) =
        companiesRepository.getSecReports(stock)

    override fun getOtcReports(stock: DomainStock) =
        companiesRepository.getOtcReports(stock)

    override fun getSymbols() =
        companiesRepository.getSymbols()

    override fun getCompanyProfile(symbols: DomainSymbols) =
        companiesRepository.getCompanyProfile(symbols)
}