package com.example.otchelper.details

import androidx.annotation.StringRes
import com.example.domain.companies.model.DomainStock
import com.example.otchelper.R

sealed class CompanyDetailsItems(@StringRes open val title: Int) {

    class Profile(
        override val title: Int = R.string.profile,
        val stock: DomainStock
    ) : CompanyDetailsItems(title)

    class Security(
        override val title: Int = R.string.security,
        val stock: DomainStock
    ) : CompanyDetailsItems(title)

}
