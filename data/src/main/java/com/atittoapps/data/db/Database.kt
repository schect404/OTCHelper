package com.atittoapps.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atittoapps.data.companies.dao.StocksDao
import com.atittoapps.data.companies.model.DbStock
import com.atittoapps.data.companies.model.WatchlistStock

@Database(entities = [WatchlistStock::class, DbStock::class], version = 7)
abstract class Database : RoomDatabase() {

    abstract fun stocksDao(): StocksDao
}