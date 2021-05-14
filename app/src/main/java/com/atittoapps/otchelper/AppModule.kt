package com.atittoapps.otchelper

import com.atittoapps.otchelper.companies.CompaniesActor
import com.atittoapps.otchelper.companies.CompaniesNavigator
import com.atittoapps.otchelper.companies.CompaniesNavigatorImpl
import com.atittoapps.otchelper.details.otcnews.OtcNewsActor
import com.atittoapps.otchelper.details.reports.sec.ReportsActor
import com.atittoapps.otchelper.filter.FiltersActor
import com.atittoapps.otchelper.search.SearchActor
import com.atittoapps.otchelper.search.SearchNavigator
import com.atittoapps.otchelper.search.SearchNavigatorImpl
import com.atittoapps.otchelper.watchlist.WatchlistActor
import com.atittoapps.otchelper.watchlist.WatchlistNavigator
import com.atittoapps.otchelper.watchlist.WatchlistNavigatorImpl
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