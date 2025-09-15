package com.example.salahtracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.salahtracker.domain.model.DailySalah
import kotlinx.coroutines.flow.Flow

@Dao
interface SalahDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySalah(dailySalah: DailySalah)

    @Update
    suspend fun updateDailySalah(dailySalah: DailySalah)

    @Delete
    suspend fun deleteDailySalah(dailySalah: DailySalah)

    @Query("SELECT * FROM daily_salah WHERE date = :date LIMIT 1")
    suspend fun getDailySalahByDate(date: String): DailySalah?

    @Query("SELECT * FROM daily_salah ORDER BY date DESC")
    fun getAllDailySalah(): Flow<List<DailySalah>>

}