package com.atittoapps.otchelper.filter.industries

import com.atittoapps.domain.companies.model.Industry

data class ChoosableIndustry(
    val industry: Industry,
    val isChecked: Boolean
)

fun List<ChoosableIndustry>.allChecked() = firstOrNull { !it.isChecked } == null
