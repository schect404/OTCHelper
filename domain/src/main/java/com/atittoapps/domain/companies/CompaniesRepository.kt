package com.atittoapps.domain.companies

import com.atittoapps.domain.companies.model.*
import kotlinx.coroutines.flow.Flow

interface CompaniesRepository {

    fun fetchPage(page: Int): Flow<SharesPage>

    fun getPrimaryFiltered(): Flow<List<DomainStock>>

    fun getFilteredCompanies(): Flow<List<DomainStock>>

    fun filterCompanies(): Flow<List<DomainStock>>

    fun getOtcCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getFullCompany(stock: DomainStock): Flow<DomainStock>

    fun getExternalCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getSecReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getOtcReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getIsCurrentPossibleBasedOnReports(stock: DomainStock): Flow<DomainStock>

    fun getCompaniesInside(stock: DomainStock): Flow<DomainStock>

    fun getHistoricalData(stock: DomainStock): Flow<DomainStock>

    fun getWatchlist(fromApp: Boolean): Flow<List<DomainStock>>

    fun addWatchlist(stock: DomainStock): Flow<DomainStock>

    fun getLevels(stock: DomainStock): Flow<List<String>>

    fun removeFromWatchlist(stock: DomainStock): Flow<DomainStock>

    suspend fun storeNewFiltered(stocks: List<DomainStock>)

    fun getFilters(): DomainFilters

    fun storeFilters(filters: DomainFilters)

    fun getSymbols(): Flow<List<DomainSymbols>>

    fun getCompanyProfile(symbols: DomainSymbols): Flow<DomainStock>

    fun updateCache(stocks: List<DomainStock>): Flow<Unit>

    fun getAllIndustries(): List<Industry>
}