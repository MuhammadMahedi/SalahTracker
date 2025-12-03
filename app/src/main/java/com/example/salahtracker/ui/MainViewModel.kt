package com.example.salahtracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.repository.SalahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SalahRepository) : ViewModel() {

    private val _dayList = MutableStateFlow<List<DailySalah>>(emptyList())
    val dayList: StateFlow<List<DailySalah>> = _dayList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Guard so we only delay on the first emission
    private var hasLoadedOnce = false

    init {
        viewModelScope.launch {
            repository.getAllDays().collect { list ->
                _dayList.value = list

                if (!hasLoadedOnce) {
                    // keep loader visible for 2 seconds so user can see it
                    delay(1000)
                    _isLoading.value = false
                    hasLoadedOnce = true
                }
            }
        }
    }

    fun getAllDays() {
        repository.getAllDays()
    }

    fun insertDay(day: DailySalah) {
        viewModelScope.launch {
            repository.insertDay(day)
        }
    }

    fun updateDay(day: DailySalah) {
        viewModelScope.launch {
            repository.updateSalah(day)
        }
    }

}