package com.example.salahtracker.domain.model

import androidx.room.TypeConverter

enum class PrayerStatus {
    Done,
    Miss,
    Qaza
}

class Converters {
    @TypeConverter
    fun fromPrayerStatus(value: PrayerStatus): String = value.name

    @TypeConverter
    fun toPrayerStatus(value: String): PrayerStatus = PrayerStatus.valueOf(value)
}