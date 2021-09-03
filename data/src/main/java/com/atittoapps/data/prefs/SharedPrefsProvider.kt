package com.atittoapps.data.prefs

import android.content.Context
import com.atittoapps.data.companies.model.DataFilters
import com.google.gson.Gson

interface SharedPrefsProvider {

    fun getIsFirstTime(): Boolean
    fun getFilters(): DataFilters
    fun getLastUpdated(): Long

    fun putIsFirstTime(isFirstTime: Boolean)
    fun putFilters(filters: DataFilters)
    fun putLastUpdated(lastUpdated: Long)

}

class SharedPrefsProviderImpl(
    private val context: Context,
    private val gson: Gson
) : SharedPrefsProvider {

    private val prefs =
        context.applicationContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    override fun getIsFirstTime(): Boolean {
        return prefs.getBoolean(IS_FIRST_TIME, true)
    }

    override fun getFilters(): DataFilters {
        return prefs.getString(FILTERS, null)?.let { gson.fromJson(it, DataFilters::class.java) } ?: DataFilters()
    }

    override fun getLastUpdated(): Long {
        return prefs.getLong(LAST_UPDATED, 0)
    }

    override fun putIsFirstTime(isFirstTime: Boolean) {
        prefs.edit().putBoolean(IS_FIRST_TIME, isFirstTime).apply()
    }

    override fun putFilters(filters: DataFilters) {
        prefs.edit().putString(FILTERS, gson.toJson(filters)).apply()
    }

    override fun putLastUpdated(lastUpdated: Long) {
        prefs.edit().putLong(LAST_UPDATED, lastUpdated).apply()
    }

    companion object {
        private const val SHARED_PREFS = "sharedPrefs"

        private const val IS_FIRST_TIME = "isFirstTime"
        private const val FILTERS = "filters"
        private const val LAST_UPDATED = "lastUpdated"
    }
}