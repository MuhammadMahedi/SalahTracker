package com.example.salahtracker.utils

import android.content.Context
import android.content.SharedPreferences

class MainSharedPref(val context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("salah_tracker_prefs", Context.MODE_PRIVATE)


    private fun save(key: String?, value: Any?) {
        val editor = getEditor()
        when {
            value is Boolean -> {
                editor.putBoolean(key, (value as Boolean?)!!)
            }
            value is Int -> {
                editor.putInt(key, (value as Int?)!!)
            }
            value is Float -> {
                editor.putFloat(key, (value as Float?)!!)
            }
            value is Long -> {
                editor.putLong(key, (value as Long?)!!)
            }
            value is String -> {
                editor.putString(key, value as String?)
            }
            value is Enum<*> -> {
                editor.putString(key, value.toString())
            }
            value != null -> {
                throw RuntimeException("Attempting to save non-supported preference")
            }
        }
        editor.commit()
    }


    operator fun <T> get(key: String?): T? {
        return sharedPreferences.all[key] as T?
    }

    operator fun <T> get(key: String?, defValue: T): T {
        val returnValue = sharedPreferences.all[key] as T?
        return returnValue ?: defValue
    }

    private fun getEditor(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }


    fun setNotificationFlag(isSet: Boolean) {
        save(AppUtils.IS_NOTIFICATION_SCHEDULED, isSet)
    }
    fun getNotificationFlag(): Boolean  {
        return sharedPreferences.getBoolean(AppUtils.IS_NOTIFICATION_SCHEDULED, false)
    }


    fun clearAllData() {
        val editor = getEditor()
        editor.clear().commit()
    }

}