package com.example.data.companies.api

import com.example.data.companies.model.CompanyDetails
import com.example.data.companies.model.CompanyInside
import com.example.data.companies.model.DataExternalNewsList
import com.example.data.companies.model.DataOtcNewsList
import com.example.data.companies.model.DataOtcReports
import com.example.data.companies.model.DataSecReports
import com.example.data.companies.model.DataSymbols
import com.example.data.companies.model.HistoricalData
import com.example.data.network.NetworkContract
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompaniesApi {

    @GET(NetworkContract.COMPANIES_FILTER)
    suspend fun filterCompanies(
        @Query("priceMin") priceMin: Double?,
        @Query("priceMax") priceMax: Double?,
        @Query("volMin") volMin: Long?,
        @Query("market") market: String,
        @Query("page") page: Int = 0
    ): String

    @GET(NetworkContract.SYMBOLS)
    suspend fun getSymbols(): List<DataSymbols>

    @GET(NetworkContract.FULL_COMPANY_PROFILE)
    suspend fun getCompanyFullProfile(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): CompanyDetails

    @GET(NetworkContract.GET_OTC_NEWS)
    suspend fun getCompanyOtcNews(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): DataOtcNewsList

    @GET(NetworkContract.GET_NEWS_EXTERNAL)
    suspend fun getCompanyExternalNews(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): DataExternalNewsList

    @GET(NetworkContract.GET_SEC_REPORT)
    suspend fun getSecReports(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): DataSecReports

    @GET(NetworkContract.GET_REPORTS)
    suspend fun getOtcReports(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): DataOtcReports

    @GET(NetworkContract.COMPANY_INSIDE)
    suspend fun getCompanyInside(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?
    ): CompanyInside

    @GET(NetworkContract.GET_HISTORICAL_DATA)
    suspend fun getHistoricalData(
        @Query("symbol") symbolQuery: String?,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("token") token: String = NetworkContract.getRandomToken()
    ): HistoricalData
}