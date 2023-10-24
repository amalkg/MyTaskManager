package com.cns.mytaskmanager.ui.main

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.cns.mytaskmanager.core.BaseActivity
import com.cns.mytaskmanager.databinding.ActivityMainBinding
import com.cns.mytaskmanager.utils.ContextUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    println("Permission granted")
                } else {
                    Snackbar.make(
                        findViewById<View>(android.R.id.content).rootView,
                        "Please grant Notification permission from App Settings",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            println("Permission granted")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun attachBaseContext(newBase: Context) {
        val localeToSwitch = Locale("en")
        val localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitch)
        super.attachBaseContext(localeUpdatedContext)
    }
}