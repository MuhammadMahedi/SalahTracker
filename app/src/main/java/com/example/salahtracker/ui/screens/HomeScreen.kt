package com.example.salahtracker.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.ui.MainViewModel
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(innerPadding: PaddingValues, viewModel: MainViewModel) {
    var showSheet by remember { mutableStateOf(false) }
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val dayList by viewModel.dayList.collectAsState()

    Log.e("HomeScreen: ", dayList.toString())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { showSheet = true }) {
                    Text("Open Bottom Sheet")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(dayList) { day ->
                ItemDailySummery(day)
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
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
                        }
                    )
                }
            }

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                onClick = {
                    val dailySalah = DailySalah(
                        date = today,
                        fajr = statuses[FAZR]!!,
                        zuhr = statuses[ZUHR]!!,
                        asr = statuses[ASR]!!,
                        maghrib = statuses[MAGHRIB]!!,
                        isha = statuses[ISHA]!!
                    )
                    saveSalahStatus(viewModel, dailySalah)
                    showSheet = false
                }) {
                Text("Save")
            }

        }
    }
}

fun saveSalahStatus(viewModel: MainViewModel, dailySalah: DailySalah) {
    viewModel.insertDay(dailySalah)
}


@Composable
fun SalahStatusInputItem(
    title: String,
    selectedStatus: PrayerStatus,
    onStatusChange: (PrayerStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prayer name
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.4f)
        )

        // Options
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(0.4f)) {
            CheckboxWithLabel(
                label = "Attend",
                checked = selectedStatus == PrayerStatus.Attend,
                onCheckedChange = { onStatusChange(PrayerStatus.Attend) }
            )
            CheckboxWithLabel(
                label = "Qaza",
                checked = selectedStatus == PrayerStatus.Qaza,
                onCheckedChange = { onStatusChange(PrayerStatus.Qaza) }
            )
            CheckboxWithLabel(
                label = "Miss",
                checked = selectedStatus == PrayerStatus.Miss,
                onCheckedChange = { onStatusChange(PrayerStatus.Miss) }
            )
        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onCheckedChange() }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
        Text(text = label)
    }
}


/*
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    //HomeScreen(innerPadding = PaddingValues(), viewModel = hiltViewModel())
    //SalahStatusItem(title = "Fajr", selectedStatus = PrayerStatus.Attend, onStatusChange = {})

}*/
