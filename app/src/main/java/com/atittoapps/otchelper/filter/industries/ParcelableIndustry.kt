package com.atittoapps.otchelper.filter.industries

import android.os.Parcelable
import com.atittoapps.domain.companies.model.Industry
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableIndustry (
    val id: String,
    val text: String
): Parcelable {
    fun toDomain() = Industry(id, text)
}

fun Industry.toParcelable() = ParcelableIndustry(id, text)