package com.atittoapps.data.companies.api

import com.atittoapps.data.companies.model.*
import com.atittoapps.data.network.NetworkContract
import retrofit2.http.*

interface CompaniesApi {

    @GET(NetworkContract.COMPANIES_FILTER)
    suspend fun filterCompanies(
        @Query("priceMin") priceMin: Double?,
        @Query("priceMax") priceMax: Double?,
        @Query("volMin") volMin: Long?,
        @Query("market") market: String,
        @Query("industry") industry: String = "",
        @Header("referer") authority: String? = "https://www.otcmarkets.com/research/stock-screener",
        @Header("origin") origin: String? = "https://www.otcmarkets.com/research/stock-screener",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): String

    @GET(NetworkContract.SYMBOLS)
    suspend fun getSymbols(
            @Header("origin") url: String = "https://www.otcmarkets.com",
            @Header("referer") referer: String = "https://www.otcmarkets.com/",
            @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"): List<DataSymbols>

    @GET(NetworkContract.LEVELS)
    suspend fun getLevels(@Query("symbol") symbol: String?,
                          @Query("token") token: String = NetworkContract.getRandomToken()): ApiLevels

    @GET(NetworkContract.FULL_COMPANY_PROFILE)
    suspend fun getCompanyFullProfile(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): CompanyDetails

    @GET(NetworkContract.GET_OTC_NEWS)
    suspend fun getCompanyOtcNews(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): DataOtcNewsList

    @GET(NetworkContract.GET_NEWS_EXTERNAL)
    suspend fun getCompanyExternalNews(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): DataExternalNewsList

    @GET(NetworkContract.GET_SEC_REPORT)
    suspend fun getSecReports(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): DataSecReports

    @GET(NetworkContract.GET_REPORTS)
    suspend fun getOtcReports(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): DataOtcReports

    @GET(NetworkContract.COMPANY_INSIDE)
    suspend fun getCompanyInside(
        @Path("symbol") symbol: String?,
        @Query("symbol") symbolQuery: String?,
        @Header("Origin") url: String = "https://www.otcmarkets.com",
        @Header("Referer") referer: String = "https://www.otcmarkets.com/",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    ): CompanyInside

    @GET(NetworkContract.GET_HISTORICAL_DATA)
    suspend fun getHistoricalData(
        @Query("symbol") symbolQuery: String?,
        @Query("from") from: String?,
        @Query("to") to: String?,
        @Query("token") token: String = NetworkContract.getRandomToken()
    ): HistoricalData
}