package com.example.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.companies.dao.StocksDao
import com.example.data.companies.model.DbStock
import com.example.data.companies.model.WatchlistStock

@Database(entities = [WatchlistStock::class, DbStock::class], version = 7)
abstract class Database : RoomDatabase() {

    abstract fun stocksDao(): StocksDao
}