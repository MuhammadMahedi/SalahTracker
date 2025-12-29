package com.example.salahtracker.utils

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.salahtracker.R

object AppUtils {

    const val FAZR = "Fazr"
    const val ZUHR = "Zuhr"
    const val ASR = "Asr"
    const val MAGHRIB = "Maghrib"
    const val ISHA = "Isha"
    const val FROM_NOTIFICATION = "FromNotification"
    const val IS_NOTIFICATION_SCHEDULED = "IsNotificationScheduled"
    const val SCHEDULED_MINUTE = "ScheduledMinute"
    const val SCHEDULED_HOUR = "ScheduledHour"

    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Exact Alarm Permission Needed")
                    .setMessage("This app requires exact alarm permission to work properly. Please allow it in settings.")
                    .setCancelable(false)
                    .setPositiveButton("Allow") { dialog, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Not now") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

                dialog.apply {

                    getButton(AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(ContextCompat.getColor(context, R.color.teal_700))

                    getButton(AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(ContextCompat.getColor(context, R.color.teal_700))

                    // Set dialog width to 70% of screen
                    val displayMetrics = context.resources.displayMetrics
                    val width = (displayMetrics.widthPixels * 0.85).toInt()
                    window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

                    // Create rounded background
                    val background = GradientDrawable().apply {
                        setColor(ContextCompat.getColor(context, android.R.color.white))
                        cornerRadius = context.resources.displayMetrics.density * 12 // 12dp radius
                    }

                    window?.setBackgroundDrawable(background)

                }




            }
        }
    }
}