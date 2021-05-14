package com.example.data

import androidx.room.Room
import com.example.data.companies.api.CompaniesApi
import com.example.data.companies.repository.CompaniesRepositoryImpl
import com.example.data.db.Database
import com.example.data.prefs.SharedPrefsProvider
import com.example.data.prefs.SharedPrefsProviderImpl
import com.example.data.retrofit.RetrofitFactory
import com.example.data.retrofit.RetrofitFactoryImpl
import com.example.domain.companies.CompaniesRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<Gson> { GsonBuilder().setLenient().create() }
    single { OkHttpClient() }
    single<RetrofitFactory> { RetrofitFactoryImpl(get()) }
    single<Retrofit> { get<RetrofitFactory>().createRetrofit(get()) }

    single { Room.databaseBuilder(androidApplication(), Database::class.java, "stocks-db").fallbackToDestructiveMigration().build() }

    single { get<Database>().stocksDao() }

    single { get<Retrofit>().create(CompaniesApi::class.java) }

    single<CompaniesRepository> { CompaniesRepositoryImpl(get(), get(), get(), get()) }

    single<SharedPrefsProvider> { SharedPrefsProviderImpl(androidApplication(), get()) }
}