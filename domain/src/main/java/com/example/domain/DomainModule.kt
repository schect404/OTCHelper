package com.example.domain

import com.example.domain.companies.CompaniesInteractor
import com.example.domain.companies.CompaniesInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    factory<CompaniesInteractor> { CompaniesInteractorImpl(get()) }
}