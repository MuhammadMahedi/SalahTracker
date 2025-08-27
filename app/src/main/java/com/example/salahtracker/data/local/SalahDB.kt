package com.example.salahtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.salahtracker.domain.model.DailySalah

@Database(entities = [DailySalah::class], version = 1, exportSchema = false)
abstract class SalahDB :RoomDatabase(){
    abstract val dao: SalahDao
}
