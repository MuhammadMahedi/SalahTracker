package com.example.salahtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.salahtracker.domain.model.Converters
import com.example.salahtracker.domain.model.DailySalah

@Database(entities = [DailySalah::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SalahDB :RoomDatabase(){
    abstract val dao: SalahDao
}
