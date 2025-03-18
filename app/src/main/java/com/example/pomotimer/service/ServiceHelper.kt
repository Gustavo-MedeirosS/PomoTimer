package com.example.pomotimer.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.pomotimer.Constants.ACTION_SERVICE_FINISH
import com.example.pomotimer.Constants.ACTION_SERVICE_PAUSE
import com.example.pomotimer.Constants.ACTION_SERVICE_RESUME
import com.example.pomotimer.Constants.ACTION_SERVICE_START
import com.example.pomotimer.Constants.CLICK_REQUEST_CODE
import com.example.pomotimer.Constants.FINISH_REQUEST_CODE
import com.example.pomotimer.Constants.PAUSE_REQUEST_CODE
import com.example.pomotimer.Constants.START_RESUME_REQUEST_CODE
import com.example.pomotimer.MainActivity

object ServiceHelper {

    private const val FLAG = PendingIntent.FLAG_IMMUTABLE

    // click intent
    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context, CLICK_REQUEST_CODE, clickIntent, FLAG
        )
    }

    // start timer
    fun startPendingIntent(context: Context): PendingIntent {
        val startIntent = Intent(context, PomotimerService::class.java).apply {
            action = ACTION_SERVICE_START
        }
        return PendingIntent.getService(
            context, START_RESUME_REQUEST_CODE, startIntent, FLAG
        )
    }

    // pause timer
    fun pausePendingIntent(context: Context): PendingIntent {
        val pauseIntent = Intent(context, PomotimerService::class.java).apply {
            action = ACTION_SERVICE_PAUSE
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, pauseIntent, FLAG
        )
    }

    // resume timer
    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, PomotimerService::class.java).apply {
            action = ACTION_SERVICE_RESUME
        }
        return PendingIntent.getService(
            context, START_RESUME_REQUEST_CODE, resumeIntent, FLAG
        )
    }

    // finish timer
    fun finishPendingIntent(context: Context): PendingIntent {
        val finishIntent = Intent(context, PomotimerService::class.java).apply {
            action = ACTION_SERVICE_FINISH
        }
        return PendingIntent.getService(
            context, FINISH_REQUEST_CODE, finishIntent, FLAG
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, PomotimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}