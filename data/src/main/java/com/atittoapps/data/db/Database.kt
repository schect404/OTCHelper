package com.atittoapps.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atittoapps.data.companies.dao.StocksDao
import com.atittoapps.data.companies.model.DbHistoricalData
import com.atittoapps.data.companies.model.DbStock
import com.atittoapps.data.companies.model.WatchlistStock

@Database(entities = [WatchlistStock::class, DbStock::class, DbHistoricalData::class], version = 9)
abstract class Database : RoomDatabase() {

    abstract fun stocksDao(): StocksDao
}