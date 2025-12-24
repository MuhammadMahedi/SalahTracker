package com.example.salahtracker.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.example.salahtracker.R
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.ui.MainViewModel
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel,showInputDialog : Boolean) {
    var showSheet by remember { mutableStateOf(showInputDialog) }
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    val dayList by viewModel.dayList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showClockDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    var selectedHour by remember {
        mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY))
    }

    var selectedMinute by remember {
        mutableStateOf(calendar.get(Calendar.MINUTE))
    }

    Log.e("HomeScreen: ", dayList.toString())

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            alpha = 0.3f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Salah Status",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = { showClockDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_schedule),
                                contentDescription = "Time",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )

            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // Blue
                            contentColor = Color.White
                        )
                    ) {
                        if(dayList.any { it.date == today}){
                            Text("Update Today's Salah Status")
                        } else{
                            Text("Add Today's Salah Status")
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                // Show loader before showing data
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    if(dayList.isEmpty()){
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Nothing added yet. Please add your today's Salah status.")
                        }
                    }else{
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(dayList) { day ->
                                ItemDailySummery(day,viewModel)
                                Log.e("HomeScreen: dates",day.date )
                            }
                        }
                    }

                }
            }

            if (showSheet) {
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false }, sheetState = sheetState
                ) {
                    val todaySalah = dayList.find { it.date == today }
                    val prayers = listOf(FAZR, ZUHR, ASR, MAGHRIB, ISHA)
                    val statuses = remember {
                        if (todaySalah != null) {
                            mutableStateMapOf(
                                FAZR to todaySalah.fajr,
                                ZUHR to todaySalah.zuhr,
                                ASR to todaySalah.asr,
                                MAGHRIB to todaySalah.maghrib,
                                ISHA to todaySalah.isha
                            )
                        } else {
                            mutableStateMapOf(
                                FAZR to PrayerStatus.Miss,
                                ZUHR to PrayerStatus.Miss,
                                ASR to PrayerStatus.Miss,
                                MAGHRIB to PrayerStatus.Miss,
                                ISHA to PrayerStatus.Miss
                            )
                        }

                    }

                    LazyColumn {
                        items(prayers) { prayer ->
                            SalahStatusInputItem(
                                title = prayer,
                                selectedStatus = statuses[prayer] ?: PrayerStatus.Miss,
                                onStatusChange = { newStatus ->
                                    statuses[prayer] = newStatus
                                })
                        }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // Blue
                            contentColor = Color.White
                        ),
                        onClick = {
                            val dailySalah = DailySalah(
                                date = today,
                                fajr = statuses[FAZR]!!,
                                zuhr = statuses[ZUHR]!!,
                                asr = statuses[ASR]!!,
                                maghrib = statuses[MAGHRIB]!!,
                                isha = statuses[ISHA]!!
                            )
                            saveSalahStatus(
                                viewModel, dailySalah, today, dayList.firstOrNull()?.date ?: today
                            )
                            showSheet = false
                        }) {
                        if(dayList.any { it.date == today}){
                            Text("Update")
                        } else {
                            Text("Save")
                        }
                    }

                }
            }

            if (showClockDialog) {
                AlertDialog(
                    onDismissRequest = { showClockDialog = false },

                    title = {
                        Text("Set time for reminders")
                    },

                    text = {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                WheelTimePicker(
                                    timeFormat = TimeFormat.AM_PM,
                                    startTime = LocalTime.of(selectedHour, selectedMinute),
                                    onSnappedTime = { snappedTime ->
                                        selectedHour = snappedTime.hour
                                        selectedMinute = snappedTime.minute
                                    }
                                )
                            } else {
                                TimePicker(
                                    state = rememberTimePickerState(
                                        initialHour = selectedHour,
                                        initialMinute = selectedMinute,
                                        is24Hour = false
                                    ),
                                    modifier = Modifier.height(200.dp)
                                )
                            }
                        }
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                // ✅ selectedHour & selectedMinute are ALWAYS valid
                                // 👉 schedule alarm / save to ViewModel
                                showClockDialog = false
                            }
                        ) {
                            Text("OK")
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = { showClockDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }



        }

    }




}


fun saveSalahStatus(
    viewModel: MainViewModel, dailySalah: DailySalah, today: String, lastSavedDate: String
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    try {
        val todayDate = dateFormat.parse(today)
        val lastDate = dateFormat.parse(lastSavedDate)

        if (todayDate != null && lastDate != null) {
            val diffMillis = todayDate.time - lastDate.time
            val dayDiff = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()

            Log.e("saveSalahStatus", "Day difference: $dayDiff")

            when {
                dayDiff <= 1 -> {
                    // Insert only today
                    viewModel.insertDay(dailySalah)
                }

                dayDiff > 1 -> {
                    // Fill missing days
                    val calendar = Calendar.getInstance()
                    calendar.time = lastDate

                    for (i in 1 until dayDiff) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        val missingDate = dateFormat.format(calendar.time)
                        val emptyDay =
                            DailySalah(date = missingDate) // assuming DailySalah has a date field
                        viewModel.insertDay(emptyDay)
                        Log.e("saveSalahStatus", "Inserted missing day: $missingDate")
                    }

                    // Finally, insert today's Salah
                    viewModel.insertDay(dailySalah)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}