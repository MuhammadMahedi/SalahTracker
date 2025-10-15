package com.example.salahtracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salahtracker.R
import com.example.salahtracker.domain.model.DailySalah
import com.example.salahtracker.domain.model.PrayerStatus
import com.example.salahtracker.utils.AppUtils.ASR
import com.example.salahtracker.utils.AppUtils.FAZR
import com.example.salahtracker.utils.AppUtils.ISHA
import com.example.salahtracker.utils.AppUtils.MAGHRIB
import com.example.salahtracker.utils.AppUtils.ZUHR

@Composable
fun ItemDailySummery(day: DailySalah) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            day.apply {

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(2f) // 👈 gives twice the space
                )

                SalahStatusItem(FAZR, fajr, modifier = Modifier.weight(1f))
                SalahStatusItem(ZUHR, zuhr, modifier = Modifier.weight(1f))
                SalahStatusItem(ASR, asr, modifier = Modifier.weight(1f))
                SalahStatusItem(MAGHRIB, maghrib, modifier = Modifier.weight(1f))
                SalahStatusItem(ISHA, isha, modifier = Modifier.weight(1f))
            }


        }


    }

}

@Composable
fun SalahStatusItem(name: String, prayerStatus: PrayerStatus, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val icon: ImageVector = when (prayerStatus) {
        PrayerStatus.Done -> ImageVector.vectorResource(id = R.drawable.ic_done)
        PrayerStatus.Miss -> ImageVector.vectorResource(id = R.drawable.ic_miss)
        PrayerStatus.Qaza -> ImageVector.vectorResource(id = R.drawable.ic_qaza)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.clickable(onClick = {
            if(prayerStatus == PrayerStatus.Miss){
                showDialog = true
            }
        })
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            modifier = Modifier.size(30.dp),
            tint = Color.Unspecified // important: keeps original SVG color
        )
        Text(text = name, fontSize = 10.sp)
    }

    ChangeStatusDialog(showDialog = showDialog) {
        showDialog = false
    }
}

@Composable
fun ChangeStatusDialog(showDialog: Boolean, onClose: () -> Unit) {
    val ctx = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text("Change Status") },
            text = { Text("Do you want to change the status from Miss to Qaza?") },
            confirmButton = {
                TextButton(onClick = {
                    Toast.makeText(ctx, "Status changed!", Toast.LENGTH_SHORT).show()
                    onClose()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Toast.makeText(ctx, "Cancelled", Toast.LENGTH_SHORT).show()
                    onClose()
                }) {
                    Text("No")
                }
            }
        )
    }
}



@Preview
@Composable
fun ItemDailySummeryPreview() {
    ItemDailySummery(
        DailySalah(
            "23 May 2023",
            PrayerStatus.Miss,
            PrayerStatus.Done,
            PrayerStatus.Qaza,
            PrayerStatus.Done,
            PrayerStatus.Done
        )
    )
}


