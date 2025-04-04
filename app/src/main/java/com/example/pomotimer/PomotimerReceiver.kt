package com.example.pomotimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pomotimer.ui.view_model.PomotimerViewModel
import com.example.pomotimer.ui.view_model.ViewModelProviderHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PomotimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action.let { action ->
            val viewModel: PomotimerViewModel? = ViewModelProviderHelper.viewModel

            viewModel?.triggerForegroundService(context = context, action = action)
        }
    }
}