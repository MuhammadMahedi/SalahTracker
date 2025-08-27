package com.example.salahtracker.domain.repository

import com.example.salahtracker.data.local.SalahDao
import com.example.salahtracker.domain.model.DailySalah
import javax.inject.Inject

class SalahRepository @Inject constructor(private val salahDao: SalahDao){

    suspend fun getAllDays() : List<DailySalah> = salahDao.getAllDailySalah()

    suspend fun insertDay(day: DailySalah) = salahDao.insertDailySalah(day)

    suspend fun deleteDay(day: DailySalah) = salahDao.deleteDailySalah(day)

    suspend fun updateSalah(day: DailySalah) = salahDao.updateDailySalah(day)

}