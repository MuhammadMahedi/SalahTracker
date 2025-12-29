package com.example.salahtracker

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.salahtracker.ui.receiver.AlarmReceiver
import com.example.salahtracker.utils.MainSharedPref
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar

@HiltAndroidApp
class MainApp :Application() {

    companion object {
        lateinit var sharedPref: MainSharedPref

        lateinit var appContext:Context

    }

    override fun onCreate() {
        super.onCreate()

        appContext = this
        sharedPref = MainSharedPref(appContext)

        if(!sharedPref.getNotificationFlag()){
            scheduleAlarm()
        }

    }

    private fun scheduleAlarm() {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, sharedPref.getScheduledHour())
            set(Calendar.MINUTE, sharedPref.getScheduledMinute())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)


            // If the time has already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }

        }

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        alarmManager.cancel(pendingIntent)
        // For exact repeating daily alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        sharedPref.setNotificationFlag(true)
        Log.e( "scheduleAlarm: ", "scheduled from MainApp")

    }

}