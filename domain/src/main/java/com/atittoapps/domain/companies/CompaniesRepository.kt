package com.atittoapps.domain.companies

import com.atittoapps.domain.companies.model.DomainFilters
import com.atittoapps.domain.companies.model.DomainOtcNews
import com.atittoapps.domain.companies.model.DomainSecReport
import com.atittoapps.domain.companies.model.DomainStock
import com.atittoapps.domain.companies.model.DomainSymbols
import com.atittoapps.domain.companies.model.SharesPage
import kotlinx.coroutines.flow.Flow

interface CompaniesRepository {

    fun fetchPage(page: Int): Flow<SharesPage>

    fun getPrimaryFiltered(): Flow<List<DomainStock>>

    fun getFilteredCompanies(): Flow<List<DomainStock>>

    fun filterCompanies(): Flow<List<DomainStock>>

    fun getOtcCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getExternalCompanyNews(stock: DomainStock): Flow<List<DomainOtcNews>>

    fun getSecReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getOtcReports(stock: DomainStock): Flow<List<DomainSecReport>>

    fun getIsCurrentPossibleBasedOnReports(stock: DomainStock): Flow<DomainStock>

    fun getCompaniesInside(stock: DomainStock): Flow<DomainStock>

    fun getHistoricalData(stock: DomainStock): Flow<DomainStock>

    fun getWatchlist(fromApp: Boolean): Flow<List<DomainStock>>

    fun addWatchlist(stock: DomainStock): Flow<DomainStock>

    fun removeFromWatchlist(stock: DomainStock): Flow<DomainStock>

    suspend fun storeNewFiltered(stocks: List<DomainStock>)

    fun getFilters(): DomainFilters

    fun storeFilters(filters: DomainFilters)

    fun getSymbols(): Flow<List<DomainSymbols>>

    fun getCompanyProfile(symbols: DomainSymbols): Flow<DomainStock>
}