package com.example.pomofocus.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.pomofocus.Constants.CLICK_REQUEST_CODE
import com.example.pomofocus.Constants.FINISH_REQUEST_CODE
import com.example.pomofocus.Constants.RESUME_REQUEST_CODE
import com.example.pomofocus.Constants.PAUSE_REQUEST_CODE
import com.example.pomofocus.Constants.TIMER_STATE
import com.example.pomofocus.MainActivity
import com.example.pomofocus.TimerState

object ServiceHelper {

    private const val FLAG = PendingIntent.FLAG_IMMUTABLE

    // start timer
    fun clickPendingIntent(context: Context): PendingIntent {
        val startIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(TIMER_STATE, TimerState.STARTED)
        }
        return PendingIntent.getService(
            context, CLICK_REQUEST_CODE, startIntent, FLAG
        )
    }

    // pause timer
    fun pausePendingIntent(context: Context): PendingIntent {
        val pauseIntent = Intent(context, PomofocusService::class.java).apply {
            putExtra(TIMER_STATE, TimerState.PAUSED)
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, pauseIntent, FLAG
        )
    }

    // resume timer
    fun startResumePendingIntent(context: Context): PendingIntent {
        val startResumeIntent = Intent(context, PomofocusService::class.java).apply {
            putExtra(TIMER_STATE, TimerState.STARTED)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, startResumeIntent, FLAG
        )
    }

    // finish timer
    fun finishPendingIntent(context: Context): PendingIntent {
        val finishIntent = Intent(context, PomofocusService::class.java).apply {
            putExtra(TIMER_STATE, TimerState.IDLE)
        }
        return PendingIntent.getService(
            context, FINISH_REQUEST_CODE, finishIntent, FLAG
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, PomofocusService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}