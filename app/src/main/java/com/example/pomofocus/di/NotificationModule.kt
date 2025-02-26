package com.example.pomofocus.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.pomofocus.Constants
import com.example.pomofocus.R
import com.example.pomofocus.service.ServiceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, Constants.SILENT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_icon)
//            .setContentTitle("Focus time")
            .setContentText("00:00")
            .setShowWhen(false)
//            .setOngoing(true)
//            .addAction(0, "Start", ServiceHelper.startPendingIntent(context = context))
            .setContentIntent(ServiceHelper.clickPendingIntent(context = context))

        return builder
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) : NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}