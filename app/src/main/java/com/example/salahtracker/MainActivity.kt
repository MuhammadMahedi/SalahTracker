package com.example.salahtracker

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.salahtracker.ui.MainViewModel
import com.example.salahtracker.ui.receiver.AlarmReceiver
import com.example.salahtracker.ui.screens.HomeScreen
import com.example.salahtracker.ui.theme.SalahTrackerTheme
import com.example.salahtracker.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            // If Android < 13 → permission doesn't exist
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                scheduleAlarm()
                return@registerForActivityResult
            }

            if (isGranted) {
                scheduleAlarm()
            } else {
                val permission = Manifest.permission.POST_NOTIFICATIONS

                // User clicked "Don't ask again"
                if (!shouldShowRequestPermissionRationale(permission)) {
                    showGoToSettingsDialog()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fromNotification = intent.getBooleanExtra(AppUtils.FROM_NOTIFICATION, false)
        setContent {
            SalahTrackerTheme {
                val viewModel: MainViewModel = hiltViewModel()
                    HomeScreen(viewModel,fromNotification)

            }
        }

        handleReminder()

    }

    private fun handleReminder(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            scheduleAlarm()
        }else{
            val permission = Manifest.permission.POST_NOTIFICATIONS

            when {
                ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleAlarm()
                }

                shouldShowRequestPermissionRationale(permission) -> {
                    showRationaleDialog()
                }

                else -> {
                    requestNotificationPermission.launch(permission)
                }
            }
        }

    }


    private fun scheduleAlarm() {

        if(MainApp.sharedPref.getNotificationFlag()){
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, MainApp.sharedPref.getScheduledHour())
            set(Calendar.MINUTE, MainApp.sharedPref.getScheduledMinute())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel any existing alarm
        alarmManager.cancel(pendingIntent)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        MainApp.sharedPref.setNotificationFlag(true)
        Log.e( "scheduleAlarm: ", "scheduled from MainActivity")

    }


    private fun showGoToSettingsDialog() {
        val dialog =AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("You have permanently denied this notification permission. Enable it from app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()

        dialog.apply {

            getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.baseColor))
            getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.cancelColor))

            // Set dialog width to 70% of screen
            val displayMetrics = context.resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.85).toInt()
            window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            // Create rounded background
            val background = GradientDrawable().apply {
                setColor(ContextCompat.getColor(context, R.color.cardBg))
                cornerRadius = context.resources.displayMetrics.density * 12 // 12dp radius
            }

            window?.setBackgroundDrawable(background)

        }

    }

    private fun showRationaleDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            scheduleAlarm()
            return
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This feature needs notification permission. Please allow it.")
            .setPositiveButton("Allow") { _, _ ->
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        dialog.apply {

            getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.baseColor))
            getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.cancelColor))

            // Set dialog width to 70% of screen
            val displayMetrics = context.resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.85).toInt()
            window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            // Create rounded background
            val background = GradientDrawable().apply {
                setColor(ContextCompat.getColor(context, R.color.cardBg))
                cornerRadius = context.resources.displayMetrics.density * 12 // 12dp radius
            }

            window?.setBackgroundDrawable(background)

        }
    }





}