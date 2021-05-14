package com.atittoapps.data.companies.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atittoapps.data.companies.model.DbStock
import com.atittoapps.data.companies.model.WatchlistStock

@Dao
interface StocksDao {

    @Query("SELECT * FROM filtered")
    suspend fun getFullStocks(): List<DbStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFiltered(dbStock: List<DbStock>)

    @Query("DELETE FROM filtered")
    suspend fun removeAllFromFiltered()

    @Query("SELECT * FROM watchlist")
    suspend fun getFullWatchlist(): List<WatchlistStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWatchlist(dbStock: List<WatchlistStock>)

    @Query("DELETE FROM watchlist")
    suspend fun removeAllFromWatchlist()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(watchlistStock: WatchlistStock)

    @Delete
    suspend fun removeFromWatchlist(watchlistStock: WatchlistStock)
}