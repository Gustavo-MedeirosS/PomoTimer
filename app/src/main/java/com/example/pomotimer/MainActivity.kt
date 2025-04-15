package com.example.pomotimer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.pomotimer.service.PomotimerService
import com.example.pomotimer.ui.screen.MainScreen
import com.example.pomotimer.ui.view_model.PomotimerViewModel
import com.example.pomotimer.ui.view_model.ViewModelProviderHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private var pomotimerService: PomotimerService? = null
    private lateinit var pomotimerViewModel: PomotimerViewModel

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PomotimerService.PomotimerBinder
            pomotimerService = binder.getService()
            pomotimerViewModel.setService(service = pomotimerService!!)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            pomotimerViewModel.setService(service = null)
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        pomotimerViewModel = ViewModelProvider(this)[PomotimerViewModel::class.java]
        ViewModelProviderHelper.viewModel = pomotimerViewModel

        setContent {
            if (isBound) {
                val windowSizeClass = calculateWindowSizeClass(this)
                MainScreen(
                    windowSizeClass = windowSizeClass
                )
            }
        }

        requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestPermissions(vararg permissions: String) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}
        requestPermissionLauncher.launch(permissions.asList().toTypedArray())
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PomotimerService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        unbindService(connection)
        isBound = false
        pomotimerViewModel.triggerForegroundService(
            context = this@MainActivity,
            action = Constants.ACTION_SERVICE_CANCEL_NOTIFICATIONS
        )
        ViewModelProviderHelper.viewModel = null
        super.onDestroy()
    }
}
