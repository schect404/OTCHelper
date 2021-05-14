package com.atittoapps.otchelper.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atittoapps.domain.companies.CompaniesInteractor
import com.atittoapps.otchelper.MainActivity
import com.atittoapps.otchelper.R
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@KoinApiExtension
class FetcherWorker(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val interactor: CompaniesInteractor = get()

    override suspend fun doWork(): Result {
        createNotificationChannel()
        return try {
            startTask()
            val favourites = interactor.getWatchlist(false).first()
            if (favourites.firstOrNull { it.isPriceChanged } != null) notifyAboutChanges()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {

        val vibrationEnabled = interactor.getFilters().shouldPushesSound
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Configure the notification channel.
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(200,200,100,200)
            notificationChannel.enableVibration(vibrationEnabled)
            if(!vibrationEnabled) notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun startTask() {
        val vibrationEnabled = interactor.getFilters().shouldPushesSound
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(appContext.getString(R.string.stocks_filter))
                .setContentText(appContext.getString(R.string.started_fetching))
                .setContentIntent(makePendingIntent())
                .apply {
                    if(!vibrationEnabled) setSound(null)
                }
        }

        notificationManager.cancelAll()
        notificationManager.notify( /*notification id*/1, notificationBuilder.build())
    }

    private fun notifyAboutChanges() {
        val vibrationEnabled = interactor.getFilters().shouldPushesSound
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(appContext.getString(R.string.stocks_filter))
                .setContentText(appContext.getString(R.string.price_changed))
                .setContentIntent(makePendingIntent())
                .apply {
                    if(!vibrationEnabled) setSound(null)
                }
        }
        notificationManager.notify( /*notification id*/2, notificationBuilder.build())
    }

    private fun makePendingIntent(): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java)
        return PendingIntent.getActivity(appContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun endTask(fetched: String, new: String) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(appContext.getString(R.string.stocks_filter))
                .setContentText(appContext.getString(R.string.fetched_stocks).format(fetched, new))
        }

        notificationManager.cancel(1)

        notificationManager.notify( /*notification id*/2, notificationBuilder.build())
    }

    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"
    }

}