package com.example.data.prefs

import android.content.Context
import com.example.data.companies.model.DataFilters
import com.google.gson.Gson

interface SharedPrefsProvider {

    fun getIsFirstTime(): Boolean
    fun getFilters(): DataFilters

    fun putIsFirstTime(isFirstTime: Boolean)
    fun putFilters(filters: DataFilters)

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

    override fun putIsFirstTime(isFirstTime: Boolean) {
        prefs.edit().putBoolean(IS_FIRST_TIME, isFirstTime).apply()
    }

    override fun putFilters(filters: DataFilters) {
        prefs.edit().putString(FILTERS, gson.toJson(filters)).apply()
    }

    companion object {
        private const val SHARED_PREFS = "sharedPrefs"

        private const val IS_FIRST_TIME = "isFirstTime"
        private const val FILTERS = "filters"
    }
}