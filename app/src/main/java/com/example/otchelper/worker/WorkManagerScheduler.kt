package com.example.otchelper.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.koin.core.component.KoinApiExtension
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    @KoinApiExtension
    fun refreshPeriodicWork(context: Context) {

        //define constraints
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshCpnWork = PeriodicWorkRequest.Builder(
            FetcherWorker::class.java,
            30, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
        )
            .setInitialDelay(0, TimeUnit.MINUTES)
            .setConstraints(myConstraints)
            .addTag("myWorkManager")
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "myWorkManager",
            ExistingPeriodicWorkPolicy.REPLACE, refreshCpnWork
        )

    }
}