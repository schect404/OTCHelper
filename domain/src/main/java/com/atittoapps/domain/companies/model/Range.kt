package com.atittoapps.domain.companies.model

data class Range (
    val min: Int,
    val max: Int
) {

    fun areSimilar(other: Range) = min == other.min && max == other.max

}