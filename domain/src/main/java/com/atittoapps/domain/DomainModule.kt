package com.atittoapps.domain

import com.atittoapps.domain.companies.CompaniesInteractor
import com.atittoapps.domain.companies.CompaniesInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    factory<CompaniesInteractor> { CompaniesInteractorImpl(get()) }
}