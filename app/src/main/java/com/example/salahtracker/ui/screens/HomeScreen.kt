package com.example.salahtracker.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    var showSheet by remember { mutableStateOf(false) }
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val dayList by viewModel.dayList.collectAsState()

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
                        Text("Open Bottom Sheet")
                    }
                }
            }
        ) { scaffoldPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(dayList) { day ->
                        ItemDailySummery(day)
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
                        Text("Save")
                    }

                }
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