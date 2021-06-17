package com.atittoapps.otchelper

import android.app.Application
import androidx.annotation.AttrRes
import com.atittoapps.data.dataModule
import com.atittoapps.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        stopKoin()
        startKoin {
            androidContext(this@MainApplication.applicationContext)
            modules(listOf(dataModule, domainModule, appModule))
            androidLogger()
        }
        setTheme(R.style.Theme_OTCHelper)
        instance = this
    }

    companion object {
        var instance: MainApplication? = null

        fun resolveColorFromAttr(@AttrRes attrRes: Int): Int {
            val color = android.util.TypedValue()
            instance?.theme?.resolveAttribute(attrRes, color, true)
            return color.data
        }
    }

}