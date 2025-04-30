package com.example.pomotimer.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.pomotimer.Constants
import com.example.pomotimer.service.ServiceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import medeiros.dev.pomotimer.R
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SilentNotificationBuilder

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AlertNotificationBuilder

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @SilentNotificationBuilder
    @ServiceScoped
    @Provides
    fun provideSilentNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, Constants.SILENT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_icon)
            .setShowWhen(false)
            .setContentIntent(ServiceHelper.clickPendingIntent(context = context))

        return builder
    }

    @AlertNotificationBuilder
    @ServiceScoped
    @Provides
    fun provideAlertNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, Constants.ALERT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_icon)
            .setShowWhen(false)
            .setContentIntent(ServiceHelper.clickPendingIntent(context = context))

        return builder
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}