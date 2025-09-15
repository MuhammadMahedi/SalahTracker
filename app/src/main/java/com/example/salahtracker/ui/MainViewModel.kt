package com.example.salahtracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.repository.SalahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SalahRepository) :ViewModel(){

    /*private var _dayList : MutableLiveData<List<DailySalah>> = MutableLiveData()
    val dayList : LiveData<List<DailySalah>> = _dayList*/

    val dayList: StateFlow<List<DailySalah>> = repository.getAllDays()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun getAllDays(){
        repository.getAllDays()
    }

    fun insertDay(day: DailySalah){
        viewModelScope.launch {
            repository.insertDay(day)
        }
    }

    fun updateDay(day: DailySalah){
        viewModelScope.launch {
            repository.updateSalah(day)
        }
    }



}