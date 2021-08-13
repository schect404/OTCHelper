package com.atittoapps.data.companies.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "historicalData", indices = [Index(value = ["symbol"], unique = true)])
data class DbHistoricalData(
    @PrimaryKey
    val symbol: String,
    val historicalData: String
) {

    fun toDataHistoricalData(gson: Gson) =
        gson.fromJson<HistoricalData>(historicalData, HistoricalData::class.java)
}

fun HistoricalData.toStringJson(gson: Gson) =
    gson.toJson(this)
