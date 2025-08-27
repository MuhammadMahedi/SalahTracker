package com.example.salahtracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_salah")
data class DailySalah(
    @PrimaryKey val date: String, // yyyy-MM-dd format
    val fajr: PrayerStatus = PrayerStatus.Miss,
    val zuhr: PrayerStatus = PrayerStatus.Miss,
    val asr: PrayerStatus = PrayerStatus.Miss,
    val maghrib: PrayerStatus = PrayerStatus.Miss,
    val isha: PrayerStatus = PrayerStatus.Miss
)
