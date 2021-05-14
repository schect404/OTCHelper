package com.example.otchelper

import android.app.Application
import androidx.annotation.AttrRes
import com.example.data.dataModule
import com.example.domain.domainModule
import com.example.otchelper.worker.WorkManagerScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level

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