package com.example.otchelper

import com.example.otchelper.companies.CompaniesActor
import com.example.otchelper.companies.CompaniesNavigator
import com.example.otchelper.companies.CompaniesNavigatorImpl
import com.example.otchelper.details.otcnews.OtcNewsActor
import com.example.otchelper.details.reports.sec.ReportsActor
import com.example.otchelper.filter.FiltersActor
import com.example.otchelper.search.SearchActor
import com.example.otchelper.search.SearchNavigator
import com.example.otchelper.search.SearchNavigatorImpl
import com.example.otchelper.watchlist.WatchlistActor
import com.example.otchelper.watchlist.WatchlistNavigator
import com.example.otchelper.watchlist.WatchlistNavigatorImpl
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    factory { StubNavigator() }
    viewModel { CompaniesActor(get(), get()) }
    viewModel { WatchlistActor(get()) }
    viewModel { FiltersActor(get()) }
    viewModel { OtcNewsActor(get()) }
    viewModel { ReportsActor(get()) }
    viewModel { SearchActor(get()) }
    factory<CompaniesNavigator> { CompaniesNavigatorImpl() }
    factory<SearchNavigator> { SearchNavigatorImpl() }
    factory<WatchlistNavigator> { WatchlistNavigatorImpl() }
}