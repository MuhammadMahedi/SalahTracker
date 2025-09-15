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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.ui.MainViewModel
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(innerPadding: PaddingValues, viewModel: MainViewModel) {
    var showSheet by remember { mutableStateOf(false) }

    val dayList by viewModel.dayList.collectAsState()

    Log.e( "HomeScreen: ",dayList.toString() )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(onClick = { showSheet = true }, modifier = Modifier.padding(16.dp)) {
            Text("Open Bottom Sheet")
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            val prayers = listOf(FAZR, ZUHR, ASR, MAGHRIB, ISHA )
            val statuses = remember {
                mutableStateMapOf(
                    FAZR to PrayerStatus.Attend,
                    ZUHR to PrayerStatus.Attend,
                    ASR to PrayerStatus.Attend,
                    MAGHRIB to PrayerStatus.Attend,
                    ISHA to PrayerStatus.Attend
                )
            }

            LazyColumn {
                items(prayers) { prayer ->
                    SalahStatusItem(
                        title = prayer,
                        selectedStatus = statuses[prayer] ?: PrayerStatus.Attend,
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
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val dailySalah = DailySalah(date = today, fajr = statuses[FAZR]!!, zuhr = statuses[ZUHR]!!, asr = statuses[ASR]!!, maghrib = statuses[MAGHRIB]!!, isha = statuses[ISHA]!!)
                    saveSalahStatus(viewModel,dailySalah)
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
fun SalahStatusItem(
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
